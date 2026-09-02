package com.cpmai.study.ui.screens

import androidx.compose.foundation.clickable
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
fun SearchScreen(repo: ContentRepository, onBack: () -> Unit, onTopic: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    val results = remember(q) { repo.search(q) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = q,
                onValueChange = { q = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Try leakage, drift, DIKUW, fraud…") },
                singleLine = true
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
                if (results.topics.isNotEmpty()) {
                    item { Text("Modules", fontWeight = FontWeight.Bold) }
                    items(results.topics) { t ->
                        Text(t.title, modifier = Modifier.clickable { onTopic(t.id) }, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (results.glossary.isNotEmpty()) {
                    item { Text("Glossary", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
                    items(results.glossary) { g ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(g.term, fontWeight = FontWeight.SemiBold)
                            Text(g.definition, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (results.cards.isNotEmpty()) {
                    item { Text("Flashcards", fontWeight = FontWeight.Bold) }
                    items(results.cards) { c ->
                        Text("${c.front} → ${c.back}", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
