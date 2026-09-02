package com.cpmai.study.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.components.BulletList
import com.cpmai.study.ui.components.parseHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    repo: ContentRepository,
    @Suppress("UNUSED_PARAMETER") progress: UserProgress,
    topicId: String,
    onBack: () -> Unit,
    onQuiz: (String) -> Unit,
    onCards: (String) -> Unit
) {
    val topic = repo.topic(topicId) ?: return
    val tabs = listOf("Notes", "Top 10", "Tips", "Glossary", "Self-check")
    var tab by remember { mutableIntStateOf(0) }
    val navy = parseHex(topic.color)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic.shortTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = navy, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Text(topic.title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            Text(topic.examWeightHint, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { i, label ->
                    FilterChip(selected = tab == i, onClick = { tab = i }, label = { Text(label) })
                }
            }
            when (tab) {
                0 -> NotesWeb(repo.notesHtml(topicId), Modifier.weight(1f))
                1 -> ScrollList(topic.top10)
                2 -> TipsPane(topic)
                3 -> GlossaryPane(repo.glossaryFor(topicId))
                4 -> Column(Modifier.padding(16.dp).weight(1f)) {
                    Text("Knowledge check — say the answer out loud, then flip to notes.", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    ScrollList(topic.knowledgeCheck)
                }
            }
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onQuiz(topicId) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Quiz, contentDescription = null)
                    Text("  Quiz")
                }
                OutlinedButton(onClick = { onCards(topicId) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Style, contentDescription = null)
                    Text("  Cards")
                }
            }
        }
    }
}

@Composable
private fun ScrollList(items: List<String>) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { BulletList(items) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun TipsPane(topic: com.cpmai.study.data.Topic) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(topic.examTips) { group ->
            Text(group.heading, fontWeight = FontWeight.Bold, color = parseHex(topic.accent))
            BulletList(group.bullets)
        }
        item {
            Text("Quick revision", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            BulletList(topic.revision)
        }
    }
}

@Composable
private fun GlossaryPane(items: List<com.cpmai.study.data.GlossaryItem>) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(items) { g ->
            Text(g.term, fontWeight = FontWeight.Bold)
            Text(g.definition)
            if (g.extra.isNotBlank()) Text(g.extra, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NotesWeb(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                setBackgroundColor(0xFFFBF7F0.toInt())
            }
        },
        update = { it.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null) }
    )
}
