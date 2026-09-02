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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.data.QuizQuestion
import com.cpmai.study.ui.components.OptionButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    repo: ContentRepository,
    store: ProgressStore,
    topicId: String?,
    examMode: Boolean,
    onDone: () -> Unit
) {
    val questions = remember(topicId, examMode) {
        when {
            examMode -> repo.quizzes.shuffled().take(20)
            topicId == "daily" -> (repo.quizzes.shuffled().take(5))
            topicId == null -> repo.quizzes.shuffled()
            else -> repo.quizzesFor(topicId).ifEmpty { repo.quizzes.filter { it.topicId == topicId } }
        }
    }
    QuizPager(
        title = when {
            examMode -> "Exam simulator"
            topicId == "daily" -> "Daily drill"
            else -> repo.topic(topicId ?: "")?.shortTitle ?: "Quiz"
        },
        questions = questions,
        store = store,
        recordExam = examMode,
        onDone = onDone
    )
}

@Composable
fun ExamScreen(repo: ContentRepository, store: ProgressStore, onDone: () -> Unit) {
    QuizScreen(repo, store, topicId = null, examMode = true, onDone = onDone)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizPager(
    title: String,
    questions: List<QuizQuestion>,
    store: ProgressStore,
    recordExam: Boolean,
    onDone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var revealed by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(questions) {
        index = 0; selected = -1; revealed = false; score = 0; finished = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (questions.isEmpty()) {
            Text("No questions yet.", modifier = Modifier.padding(padding).padding(16.dp))
            return@Scaffold
        }
        if (finished) {
            val pct = (100 * score / questions.size)
            Column(Modifier.padding(padding).padding(24.dp)) {
                Text("Score", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$score / ${questions.size}  ($pct%)", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text(
                    when {
                        pct >= 85 -> "Exam-ready on this set. Review any misses in the explanations."
                        pct >= 70 -> "Solid. Drill the weak module next — don't only re-read notes."
                        else -> "Treat this as a map of gaps. Open Top 10 for those topics tonight."
                    }
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Back") }
            }
            return@Scaffold
        }

        val q = questions[index]
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            LinearProgressIndicator(progress = { (index + 1f) / questions.size }, modifier = Modifier.fillMaxWidth())
            Text("Question ${index + 1} of ${questions.size}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
            Text(q.question, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            q.options.forEachIndexed { i, opt ->
                if (opt.isNotBlank()) {
                    OptionButton(
                        letter = ('A' + i).toString(),
                        text = opt,
                        selected = selected == i,
                        revealed = revealed,
                        isCorrect = i == q.correctIndex,
                        onClick = { selected = i }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            if (revealed) {
                Text("Why", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Text(q.explanation, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!revealed) {
                    Button(
                        onClick = {
                            if (selected < 0) return@Button
                            revealed = true
                            val ok = selected == q.correctIndex
                            if (ok) score++
                            scope.launch { store.recordQuiz(q.id, ok) }
                        },
                        enabled = selected >= 0,
                        modifier = Modifier.weight(1f)
                    ) { Text("Check") }
                } else {
                    Button(
                        onClick = {
                            if (index == questions.lastIndex) {
                                finished = true
                                if (recordExam) {
                                    val pct = (100 * score / questions.size)
                                    scope.launch { store.recordExam(pct) }
                                }
                            } else {
                                index++
                                selected = -1
                                revealed = false
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (index == questions.lastIndex) "Finish" else "Next") }
                }
                OutlinedButton(onClick = onDone) { Text("Exit") }
            }
        }
    }
}
