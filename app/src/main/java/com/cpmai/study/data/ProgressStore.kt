package com.cpmai.study.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.ZoneOffset

class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("cpmai_progress", Context.MODE_PRIVATE)
    private val _progress = MutableStateFlow(read())
    val progress: StateFlow<UserProgress> = _progress.asStateFlow()

    suspend fun toggleMastered(id: String) {
        val set = _progress.value.masteredCards.toMutableSet()
        if (!set.add(id)) set.remove(id)
        write(_progress.value.copy(masteredCards = set))
        touchStudy()
    }

    suspend fun toggleBookmark(id: String) {
        val set = _progress.value.bookmarkedCards.toMutableSet()
        if (!set.add(id)) set.remove(id)
        write(_progress.value.copy(bookmarkedCards = set))
    }

    suspend fun recordQuiz(questionId: String, correct: Boolean) {
        val map = _progress.value.quizAttempts.toMutableMap()
        map[questionId] = if (correct) 1 else 0
        write(_progress.value.copy(quizAttempts = map))
        touchStudy()
    }

    suspend fun recordExam(percent: Int) {
        val list = (_progress.value.examScores + percent).takeLast(20)
        write(_progress.value.copy(examScores = list))
        touchStudy()
    }

    suspend fun addStudyMinutes(mins: Int) {
        write(_progress.value.copy(studyMinutes = _progress.value.studyMinutes + mins))
        touchStudy()
    }

    private fun touchStudy() {
        val today = LocalDate.now(ZoneOffset.UTC).toEpochDay()
        val cur = _progress.value
        val streak = when {
            cur.lastStudyEpochDay == today -> cur.streak.coerceAtLeast(1)
            cur.lastStudyEpochDay == today - 1 -> cur.streak + 1
            else -> 1
        }
        write(cur.copy(lastStudyEpochDay = today, streak = streak))
    }

    private fun read(): UserProgress {
        return UserProgress(
            masteredCards = prefs.getStringSet("mastered", emptySet()) ?: emptySet(),
            bookmarkedCards = prefs.getStringSet("bookmarks", emptySet()) ?: emptySet(),
            quizAttempts = parseMap(prefs.getString("quiz", "") ?: ""),
            examScores = (prefs.getString("exams", "") ?: "").split(",").mapNotNull { it.toIntOrNull() },
            lastStudyEpochDay = prefs.getLong("last_day", 0L),
            streak = prefs.getInt("streak", 0),
            studyMinutes = prefs.getInt("minutes", 0)
        )
    }

    private fun write(value: UserProgress) {
        prefs.edit()
            .putStringSet("mastered", value.masteredCards)
            .putStringSet("bookmarks", value.bookmarkedCards)
            .putString("quiz", value.quizAttempts.entries.joinToString(";") { "${it.key}=${it.value}" })
            .putString("exams", value.examScores.joinToString(","))
            .putLong("last_day", value.lastStudyEpochDay)
            .putInt("streak", value.streak)
            .putInt("minutes", value.studyMinutes)
            .apply()
        _progress.value = value
    }

    private fun parseMap(raw: String): Map<String, Int> {
        if (raw.isBlank()) return emptyMap()
        return raw.split(";").mapNotNull { part ->
            val bits = part.split("=")
            if (bits.size == 2) bits[0] to (bits[1].toIntOrNull() ?: return@mapNotNull null) else null
        }.toMap()
    }
}
