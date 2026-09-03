package com.cpmai.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cpmai.study.data.ContentRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(repo: ContentRepository, onBack: () -> Unit) {
    var q by remember { mutableStateOf("") }
    val items = remember(q) {
        val n = q.trim().lowercase()
        if (n.isBlank()) repo.glossary else repo.glossary.filter {
            it.term.lowercase().contains(n) || it.definition.lowercase().contains(n)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Glossary") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(q, { q = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Filter terms") }, singleLine = true)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(top = 12.dp)) {
                items(items) { g ->
                    val topic = repo.topic(g.topicId)?.shortTitle ?: g.topicId
                    Text(g.term, fontWeight = FontWeight.Bold)
                    Text(topic, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Text(g.definition)
                    if (g.extra.isNotBlank()) Text(g.extra, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
