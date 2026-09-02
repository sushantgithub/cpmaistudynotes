package com.cpmai.study.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneOffset

private val Context.dataStore by preferencesDataStore("cpmai_progress")

class ProgressStore(private val context: Context) {
    private val mastered = stringSetPreferencesKey("mastered_cards")
    private val bookmarks = stringSetPreferencesKey("bookmarked_cards")
    private val quizCsv = stringPreferencesKey("quiz_attempts")
    private val examsCsv = stringPreferencesKey("exam_scores")
    private val lastDay = longPreferencesKey("last_study_day")
    private val streakKey = intPreferencesKey("streak")
    private val minutesKey = intPreferencesKey("study_minutes")

    val progress: Flow<UserProgress> = context.dataStore.data.map { prefs ->
        UserProgress(
            masteredCards = prefs[mastered] ?: emptySet(),
            bookmarkedCards = prefs[bookmarks] ?: emptySet(),
            quizAttempts = parseMap(prefs[quizCsv] ?: ""),
            examScores = (prefs[examsCsv] ?: "").split(",").mapNotNull { it.toIntOrNull() },
            lastStudyEpochDay = prefs[lastDay] ?: 0L,
            streak = prefs[streakKey] ?: 0,
            studyMinutes = prefs[minutesKey] ?: 0
        )
    }

    suspend fun toggleMastered(id: String) {
        context.dataStore.edit { prefs ->
            val set = (prefs[mastered] ?: emptySet()).toMutableSet()
            if (!set.add(id)) set.remove(id)
            prefs[mastered] = set
        }
        touchStudy()
    }

    suspend fun toggleBookmark(id: String) {
        context.dataStore.edit { prefs ->
            val set = (prefs[bookmarks] ?: emptySet()).toMutableSet()
            if (!set.add(id)) set.remove(id)
            prefs[bookmarks] = set
        }
    }

    suspend fun recordQuiz(questionId: String, correct: Boolean) {
        context.dataStore.edit { prefs ->
            val map = parseMap(prefs[quizCsv] ?: "").toMutableMap()
            map[questionId] = if (correct) 1 else 0
            prefs[quizCsv] = map.entries.joinToString(";") { "${it.key}=${it.value}" }
        }
        touchStudy()
    }

    suspend fun recordExam(percent: Int) {
        context.dataStore.edit { prefs ->
            val list = ((prefs[examsCsv] ?: "").split(",").mapNotNull { it.toIntOrNull() } + percent).takeLast(20)
            prefs[examsCsv] = list.joinToString(",")
        }
        touchStudy()
    }

    suspend fun addStudyMinutes(mins: Int) {
        context.dataStore.edit { prefs ->
            prefs[minutesKey] = (prefs[minutesKey] ?: 0) + mins
        }
        touchStudy()
    }

    private suspend fun touchStudy() {
        val today = LocalDate.now(ZoneOffset.UTC).toEpochDay()
        context.dataStore.edit { prefs ->
            val last = prefs[lastDay] ?: 0L
            val streak = prefs[streakKey] ?: 0
            prefs[streakKey] = when {
                last == today -> streak.coerceAtLeast(1)
                last == today - 1 -> streak + 1
                else -> 1
            }
            prefs[lastDay] = today
        }
    }

    private fun parseMap(raw: String): Map<String, Int> {
        if (raw.isBlank()) return emptyMap()
        return raw.split(";").mapNotNull { part ->
            val bits = part.split("=")
            if (bits.size == 2) bits[0] to (bits[1].toIntOrNull() ?: return@mapNotNull null) else null
        }.toMap()
    }
}
