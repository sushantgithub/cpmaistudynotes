package com.cpmai.study.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhaseMapScreen(repo: ContentRepository, store: ProgressStore, onBack: () -> Unit) {
    var mode by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Six phases") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Map") })
                FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("Which phase?") })
            }
            if (mode == 0) {
                Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("CPMAI is iterative. You can return to an earlier phase when evidence says so — Think Big, Start Small, Iterate Often.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    repo.phases.forEach { ph ->
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp)) {
                            Text("Phase ${ph.number} · ${ph.name}", fontWeight = FontWeight.ExtraBold, color = Navy)
                            Text(ph.goal, modifier = Modifier.padding(top = 4.dp))
                            Text("PM moves", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                            ph.pmMoves.forEach { Text("• $it") }
                            Text("Exit: ${ph.exitCriteria}", modifier = Modifier.padding(top = 8.dp), fontSize = 13.sp)
                            Text("Fails when: ${ph.failsWhen}", color = Color(0xFFB45309), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                PhaseGame(repo, store)
            }
        }
    }
}

@Composable
private fun PhaseGame(repo: ContentRepository, store: ProgressStore) {
    val items = remember { repo.trainer.phaseScenarios.shuffled() }
    var index by remember { mutableIntStateOf(0) }
    var picked by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (done) {
        Column(Modifier.padding(24.dp)) {
            Text("$score / ${items.size}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Button(onClick = { index = 0; picked = -1; score = 0; done = false }) { Text("Again") }
        }
        return
    }
    val s = items[index]
    val revealed = picked >= 0
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(s.prompt, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        repo.phases.forEach { ph ->
            val correct = ph.number == s.correctPhase
            val bg = when {
                revealed && correct -> Color(0xFFD1FAE5)
                revealed && picked == ph.number && !correct -> Color(0xFFFEE2E2)
                else -> Color.White
            }
            Text(
                "Phase ${ph.number} · ${ph.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(enabled = !revealed) { picked = ph.number }
                    .padding(12.dp)
            )
        }
        if (revealed) {
            Text(s.hint)
            Button(onClick = {
                val ok = picked == s.correctPhase
                if (ok) score++
                scope.launch { store.recordQuiz("phase-${s.id}", ok) }
                if (index == items.lastIndex) done = true else { index++; picked = -1 }
            }) { Text(if (index == items.lastIndex) "Finish" else "Next") }
        }
    }
}
