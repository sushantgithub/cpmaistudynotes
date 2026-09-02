package com.cpmai.study.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.Topic

fun parseHex(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) {
    Color(0xFF16305C)
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TopicCard(topic: Topic, progress: Float, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val accent = parseHex(topic.accent)
    val navy = parseHex(topic.color)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(navy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = topic.phase?.toString() ?: "★",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(topic.shortTitle, fontWeight = FontWeight.ExtraBold, color = navy)
                Text(topic.subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.18f)
                )
            }
        }
    }
}

@Composable
fun OptionButton(
    letter: String,
    text: String,
    selected: Boolean,
    revealed: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        revealed && isCorrect -> Color(0xFFD1FAE5)
        revealed && selected && !isCorrect -> Color(0xFFFEE2E2)
        selected -> Color(0xFFFDF3E1)
        else -> Color.White
    }
    val border = when {
        revealed && isCorrect -> Color(0xFF059669)
        revealed && selected && !isCorrect -> Color(0xFFDC2626)
        selected -> Color(0xFFE8930C)
        else -> Color(0xFFE4E1D8)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(enabled = !revealed, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(border),
            contentAlignment = Alignment.Center
        ) {
            Text(letter, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f), fontSize = 15.sp)
    }
}

@Composable
fun RowScope.HomeQuickAction(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
    }
}

@Composable
fun EmptyHint(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(24.dp))
}

@Composable
fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { i, line ->
            Row(verticalAlignment = Alignment.Top) {
                Text("▸", color = Color(0xFFE8930C), fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text("${i + 1}. $line", modifier = Modifier.weight(1f), fontSize = 15.sp)
            }
        }
    }
}
