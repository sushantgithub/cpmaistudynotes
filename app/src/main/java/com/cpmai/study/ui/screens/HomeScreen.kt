package com.cpmai.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cpmai.study.R
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.components.HomeQuickAction
import com.cpmai.study.ui.components.SectionLabel
import com.cpmai.study.ui.components.StatChip
import com.cpmai.study.ui.components.TopicCard
import com.cpmai.study.ui.theme.Navy
import com.cpmai.study.ui.theme.NavyMid
import com.cpmai.study.ui.theme.Saffron

@Composable
fun HomeScreen(repo: ContentRepository, progress: UserProgress, nav: NavController) {
    val mastered = progress.masteredCards.size
    val totalCards = repo.flashcards.size
    val answered = progress.quizAttempts.size
    val correct = progress.quizAttempts.values.count { it == 1 }
    val accuracy = if (answered == 0) 0 else (100 * correct / answered)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Navy, NavyMid)))
                .padding(20.dp)
        ) {
            Text("UNOFFICIAL", color = Saffron, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
            Text("CPMAI Prep App", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp)
            Text(
                "Independent study aid. Not affiliated with, endorsed by, or sponsored by PMI.",
                color = Color(0xFFC6D4EC),
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("Day streak", "${progress.streak}", Saffron)
                StatChip("Cards in", "$mastered/$totalCards", Color(0xFF34D399))
                StatChip("Quiz acc.", "$accuracy%", Color(0xFF93C5FD))
            }
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeQuickAction("Exam sim", "20 mixed MCQs", Navy) { nav.navigate("exam") }
                HomeQuickAction("Daily drill", "Cards + quiz mix", Color(0xFF0F766E)) {
                    nav.navigate("quiz/daily")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeQuickAction("Phase map", "Which phase?", Color(0xFF4C1D95)) { nav.navigate("phases") }
                HomeQuickAction("Search", "Terms & notes", Color(0xFF9A3412)) { nav.navigate("search") }
            }

            SectionLabel("Continue a module")
            repo.topics.take(4).forEach { topic ->
                val cards = repo.cardsFor(topic.id)
                val done = cards.count { it.id in progress.masteredCards }
                val p = if (cards.isEmpty()) 0f else done.toFloat() / cards.size
                TopicCard(topic, p, onClick = { nav.navigate("topic/${topic.id}") })
            }
            TextButton(onClick = { nav.navigate("learn") }) { Text("See all 8 modules") }

            Button(
                onClick = { nav.navigate("progress") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                Text("Progress & weak spots")
            }
            TextButton(onClick = { nav.navigate("glossary") }, modifier = Modifier.fillMaxWidth()) {
                Text("Open full glossary (${repo.glossary.size} terms)")
            }
            Text(
                stringResource(R.string.legal_attribution),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            TextButton(onClick = { nav.navigate("legal") }, modifier = Modifier.fillMaxWidth()) {
                Text("Legal & unofficial notice")
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
