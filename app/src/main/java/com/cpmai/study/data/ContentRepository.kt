package com.cpmai.study.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContentRepository(private val context: Context) {
    private val gson = Gson()

    val topics: List<Topic> by lazy { readList("topics.json") }
    val quizzes: List<QuizQuestion> by lazy { readList("quizzes.json") }
    val flashcards: List<Flashcard> by lazy { readList("flashcards.json") }
    val glossary: List<GlossaryItem> by lazy { readList("glossary.json") }
    val patterns: List<AiPattern> by lazy { readList("patterns.json") }
    val phases: List<PhaseInfo> by lazy { readList("phases.json") }
    val trainer: TrainerPack by lazy {
        context.assets.open("trainer.json").bufferedReader().use {
            gson.fromJson(it, TrainerPack::class.java)
        }
    }

    private val notesCss: String by lazy {
        context.assets.open("notes_wrapper.css").bufferedReader().use { it.readText() }
    }

    fun topic(id: String): Topic? = topics.find { it.id == id }

    fun quizzesFor(topicId: String): List<QuizQuestion> =
        quizzes.filter { it.topicId == topicId }

    fun cardsFor(topicId: String): List<Flashcard> =
        flashcards.filter { it.topicId == topicId }

    fun glossaryFor(topicId: String): List<GlossaryItem> =
        glossary.filter { it.topicId == topicId }

    fun notesHtml(topicId: String): String {
        val body = context.assets.open("notes/$topicId.html").bufferedReader().use { it.readText() }
        return """
            <!DOCTYPE html><html><head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1"/>
            <style>$notesCss</style>
            </head><body>$body</body></html>
        """.trimIndent()
    }

    fun search(query: String): SearchBundle {
        val q = query.trim().lowercase()
        if (q.length < 2) return SearchBundle()
        return SearchBundle(
            topics = topics.filter {
                it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q) ||
                    it.revision.any { line -> line.lowercase().contains(q) } ||
                    it.top10.any { line -> line.lowercase().contains(q) }
            },
            glossary = glossary.filter {
                it.term.lowercase().contains(q) || it.definition.lowercase().contains(q)
            }.take(40),
            cards = flashcards.filter {
                it.front.lowercase().contains(q) || it.back.lowercase().contains(q)
            }.take(30),
            quizzes = quizzes.filter { it.question.lowercase().contains(q) }.take(20)
        )
    }

    private inline fun <reified T> readList(path: String): List<T> {
        val type = TypeToken.getParameterized(List::class.java, T::class.java).type
        return context.assets.open(path).bufferedReader().use { gson.fromJson(it, type) }
    }
}

data class SearchBundle(
    val topics: List<Topic> = emptyList(),
    val glossary: List<GlossaryItem> = emptyList(),
    val cards: List<Flashcard> = emptyList(),
    val quizzes: List<QuizQuestion> = emptyList()
)
