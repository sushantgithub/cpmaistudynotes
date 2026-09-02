package com.cpmai.study.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.UserProgress

@Composable
fun PracticeHubScreen(repo: ContentRepository, progress: UserProgress, nav: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Practice", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(
                "Scenario MCQs from the notes plus extra traps written for the exam. Accuracy is stored so Home can show weak spots.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            )
        }
        item {
            PracticeRow("Exam simulator", "20 mixed questions · scored like a mini mock") { nav.navigate("exam") }
        }
        item {
            PracticeRow("Daily drill", "5 random questions across the syllabus") { nav.navigate("quiz/daily") }
        }
        items(repo.topics) { topic ->
            val qs = repo.quizzesFor(topic.id)
            val answered = qs.count { it.id in progress.quizAttempts }
            val right = qs.count { progress.quizAttempts[it.id] == 1 }
            PracticeRow(
                topic.title,
                "${qs.size} questions · last pass $right/$answered answered"
            ) { nav.navigate("quiz/${topic.id}") }
        }
    }
}

@Composable
private fun PracticeRow(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
