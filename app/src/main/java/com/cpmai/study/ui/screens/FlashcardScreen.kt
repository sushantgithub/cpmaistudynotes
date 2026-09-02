package com.cpmai.study.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.Entitlement
import com.cpmai.study.data.Flashcard
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.theme.Navy
import com.cpmai.study.ui.theme.Saffron
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    repo: ContentRepository,
    store: ProgressStore,
    progress: UserProgress,
    topicId: String?,
    onBack: (() -> Unit)?
) {
    val scope = rememberCoroutineScope()
    var onlyBookmarks by remember { mutableStateOf(false) }
    var onlyWeak by remember { mutableStateOf(false) }
    var shuffled by remember { mutableStateOf(false) }

    val base = remember(topicId, progress.fullUnlocked) {
        val all = if (topicId == null) repo.flashcards else repo.cardsFor(topicId)
        if (progress.fullUnlocked) all else all.filter { it.topicId in Entitlement.freeTopicIds }
    }
    val cards = remember(base, onlyBookmarks, onlyWeak, shuffled, progress.bookmarkedCards, progress.masteredCards) {
        var list = base
        if (onlyBookmarks) list = list.filter { it.id in progress.bookmarkedCards }
        if (onlyWeak) list = list.filter { it.id !in progress.masteredCards }
        if (shuffled) list = list.shuffled()
        list
    }

    var index by remember(cards) { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    val card = cards.getOrNull(index.coerceAtMost(cards.lastIndex.coerceAtLeast(0)))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (topicId == null) "Flashcards" else repo.topic(topicId)?.shortTitle ?: "Cards") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { shuffled = !shuffled; index = 0; flipped = false }) {
                        Icon(Icons.Outlined.Shuffle, contentDescription = "Shuffle")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = onlyBookmarks, onClick = { onlyBookmarks = !onlyBookmarks; index = 0 }, label = { Text("Bookmarks") })
                FilterChip(selected = onlyWeak, onClick = { onlyWeak = !onlyWeak; index = 0 }, label = { Text("Not mastered") })
            }
            Spacer(Modifier.height(8.dp))
            if (cards.isEmpty()) {
                Text("No cards in this filter. Master fewer cards or add bookmarks from a deck.")
                return@Column
            }
            LinearProgressIndicator(
                progress = { (index + 1f) / cards.size },
                modifier = Modifier.fillMaxWidth()
            )
            Text("${index + 1} / ${cards.size}", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))

            if (card != null) {
                FlipCard(card, flipped, onFlip = { flipped = !flipped }, modifier = Modifier.weight(1f))
                val mastered = card.id in progress.masteredCards
                val bookmarked = card.id in progress.bookmarkedCards
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { scope.launch { store.toggleBookmark(card.id) } }) {
                        Icon(if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = Saffron)
                    }
                    IconButton(onClick = { scope.launch { store.toggleMastered(card.id) } }) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = "Mastered", tint = if (mastered) ColorSuccess else MaterialTheme.colorScheme.outline)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        index = (index - 1).coerceAtLeast(0)
                        flipped = false
                    }, modifier = Modifier.weight(1f), enabled = index > 0) { Text("Previous") }
                    Button(onClick = {
                        if (index < cards.lastIndex) {
                            index++
                            flipped = false
                        }
                    }, modifier = Modifier.weight(1f)) { Text(if (index == cards.lastIndex) "Done" else "Next") }
                }
            }
        }
    }
}

private val ColorSuccess = androidx.compose.ui.graphics.Color(0xFF059669)

@Composable
private fun FlipCard(card: Flashcard, flipped: Boolean, onFlip: () -> Unit, modifier: Modifier = Modifier) {
    val rotation by animateFloatAsState(if (flipped) 180f else 0f, label = "flip")
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable(onClick = onFlip),
        colors = CardDefaults.cardColors(containerColor = if (flipped) Navy else androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            val showBack = rotation > 90f
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer {
                rotationY = if (showBack) 180f else 0f
            }) {
                Text(if (showBack) "ANSWER" else "PROMPT", color = Saffron, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    if (showBack) card.back else card.front,
                    color = if (showBack) androidx.compose.ui.graphics.Color.White else Navy,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text("Tap to flip", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
        }
    }
}
