package com.cpmai.study.data

data class Topic(
    val id: String,
    val title: String,
    val subtitle: String,
    val shortTitle: String,
    val phase: Int?,
    val color: String,
    val accent: String,
    val icon: String,
    val examWeightHint: String,
    val top10: List<String> = emptyList(),
    val revision: List<String> = emptyList(),
    val knowledgeCheck: List<String> = emptyList(),
    val examTips: List<TipGroup> = emptyList(),
    val quizCount: Int = 0,
    val flashcardCount: Int = 0
)

data class TipGroup(
    val heading: String = "",
    val bullets: List<String> = emptyList()
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val topicId: String,
    val source: String = "notes"
)

data class Flashcard(
    val id: String,
    val front: String,
    val back: String,
    val topicId: String
)

data class GlossaryItem(
    val term: String,
    val definition: String,
    val extra: String = "",
    val topicId: String
)

data class AiPattern(
    val id: Int,
    val name: String,
    val tagline: String,
    val summary: String,
    val examples: List<String>,
    val notThis: String,
    val typicalData: String,
    val examTrap: String,
    val keywords: List<String>
)

data class PhaseInfo(
    val id: String,
    val number: Int,
    val name: String,
    val goal: String,
    val pmMoves: List<String>,
    val exitCriteria: String,
    val failsWhen: String
)

data class PatternScenario(
    val id: String,
    val prompt: String,
    val correctPatternId: Int,
    val hint: String
)

data class PhaseScenario(
    val id: String,
    val prompt: String,
    val correctPhase: Int,
    val hint: String
)

data class TrainerPack(
    val patternScenarios: List<PatternScenario> = emptyList(),
    val phaseScenarios: List<PhaseScenario> = emptyList()
)

data class UserProgress(
    val masteredCards: Set<String> = emptySet(),
    val bookmarkedCards: Set<String> = emptySet(),
    val quizAttempts: Map<String, Int> = emptyMap(), // questionId -> last correct 1/0
    val examScores: List<Int> = emptyList(),
    val lastStudyEpochDay: Long = 0,
    val streak: Int = 0,
    val studyMinutes: Int = 0,
    val disclaimerAccepted: Boolean = false
)
