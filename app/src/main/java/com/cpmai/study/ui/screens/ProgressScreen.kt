package com.cpmai.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.components.StatChip
import com.cpmai.study.ui.theme.Navy
import com.cpmai.study.ui.theme.Saffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(repo: ContentRepository, progress: UserProgress, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Progress") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("Streak", "${progress.streak}d", Saffron)
                StatChip("Mastered", "${progress.masteredCards.size}", Navy)
                StatChip("Mocks", "${progress.examScores.size}", androidx.compose.ui.graphics.Color(0xFF0F766E))
            }
            if (progress.examScores.isNotEmpty()) {
                Text("Recent exam simulator %", fontWeight = FontWeight.Bold)
                Text(progress.examScores.takeLast(8).joinToString(" → ") { "$it%" })
            }
            Text("Module mastery (flashcards)", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
            repo.topics.forEach { topic ->
                val cards = repo.cardsFor(topic.id)
                val done = cards.count { it.id in progress.masteredCards }
                val frac = if (cards.isEmpty()) 0f else done.toFloat() / cards.size
                Text("${topic.shortTitle}  $done/${cards.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(progress = { frac }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
            }
            Text("Quiz misses by module", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            repo.topics.forEach { topic ->
                val qs = repo.quizzesFor(topic.id)
                val wrong = qs.count { progress.quizAttempts[it.id] == 0 }
                val right = qs.count { progress.quizAttempts[it.id] == 1 }
                if (wrong + right > 0) {
                    Text("${topic.shortTitle}: $right correct, $wrong to retry")
                }
            }
            val weakest = repo.topics.maxByOrNull { topic ->
                repo.quizzesFor(topic.id).count { progress.quizAttempts[it.id] == 0 }
            }
            if (weakest != null) {
                Text("Suggested next: ${weakest.title}", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                Text(weakest.examWeightHint)
            }
        }
    }
}
