package com.cpmai.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.components.TopicCard

@Composable
fun LearnScreen(repo: ContentRepository, progress: UserProgress, onOpen: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Syllabus", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(
                "Study in order: foundations, then Phases I–VI, then the 7 Patterns. Each module has notes, Top 10, glossary, flashcards, and scenario MCQs.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            )
        }
        items(repo.topics, key = { it.id }) { topic ->
            val cards = repo.cardsFor(topic.id)
            val done = cards.count { it.id in progress.masteredCards }
            val p = if (cards.isEmpty()) 0f else done.toFloat() / cards.size
            TopicCard(topic, p, onClick = { onOpen(topic.id) })
        }
    }
}
