package com.cpmai.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.ui.theme.Navy
import com.cpmai.study.ui.theme.SoftSaffron
import kotlinx.coroutines.launch

@Composable
fun PatternLabScreen(repo: ContentRepository, store: ProgressStore) {
    var mode by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Text("7 Patterns of AI", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 4.dp))
        Text("Tap a pattern to study it, then play Identify the pattern.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Explorer") })
            FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("Identify") })
        }
        if (mode == 0) Explorer(repo) else PatternGame(repo, store)
    }
}

@Composable
private fun Explorer(repo: ContentRepository) {
    var selected by remember { mutableIntStateOf(1) }
    val p = repo.patterns.find { it.id == selected } ?: return
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repo.patterns.forEach { pat ->
                val on = pat.id == selected
                Text(
                    "${pat.id}",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (on) Navy else Color(0xFFE5E7EB))
                        .clickable { selected = pat.id }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (on) Color.White else Navy,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(p.name, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Navy)
        Text(p.tagline, color = Color(0xFFE8930C), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(p.summary, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        LabelBlock("Typical examples", p.examples.joinToString(" · "))
        LabelBlock("Not this", p.notThis)
        LabelBlock("Typical data", p.typicalData)
        LabelBlock("Exam trap", p.examTrap)
        LabelBlock("Cue words", p.keywords.joinToString(", "))
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabelBlock(title: String, body: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SoftSaffron)
            .padding(12.dp)
    ) {
        Text(title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text(body, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun PatternGame(repo: ContentRepository, store: ProgressStore) {
    val items = remember { repo.trainer.patternScenarios.shuffled() }
    var index by remember { mutableIntStateOf(0) }
    var picked by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (items.isEmpty()) {
        Text("Trainer data missing.", modifier = Modifier.padding(16.dp))
        return
    }
    if (done) {
        Column(Modifier.padding(24.dp)) {
            Text("Pattern score", fontWeight = FontWeight.Bold)
            Text("$score / ${items.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Button(onClick = {
                index = 0; picked = -1; score = 0; done = false
            }, modifier = Modifier.padding(top = 12.dp)) { Text("Play again") }
        }
        return
    }
    val s = items[index]
    val revealed = picked >= 0
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Scenario ${index + 1} / ${items.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(s.prompt, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        repo.patterns.forEach { pat ->
            val correct = pat.id == s.correctPatternId
            val bg = when {
                revealed && correct -> Color(0xFFD1FAE5)
                revealed && picked == pat.id && !correct -> Color(0xFFFEE2E2)
                else -> Color.White
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(enabled = !revealed) { picked = pat.id }
                    .padding(12.dp)
            ) {
                Text("${pat.id}  ${pat.name}", fontWeight = FontWeight.Medium)
            }
        }
        if (revealed) {
            Text("Hint: ${s.hint}")
            Button(onClick = {
                val ok = picked == s.correctPatternId
                if (ok) score++
                scope.launch { store.recordQuiz("pattern-${s.id}", ok) }
                if (index == items.lastIndex) done = true else {
                    index++; picked = -1
                }
            }) { Text(if (index == items.lastIndex) "Finish" else "Next") }
        }
    }
}
