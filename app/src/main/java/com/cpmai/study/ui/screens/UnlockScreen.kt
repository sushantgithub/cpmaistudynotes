package com.cpmai.study.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.sp
import com.cpmai.study.data.Entitlement
import com.cpmai.study.data.LicenseKeys
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.theme.Navy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockScreen(store: ProgressStore, progress: UserProgress, onBack: () -> Unit) {
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var ok by remember { mutableStateOf(progress.fullUnlocked) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Full version") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (ok) {
                Text("Full version unlocked", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Navy)
                Spacer(Modifier.height(8.dp))
                Text("All modules, the exam simulator, and the full card deck are available on this device.")
            } else {
                Text("Free vs full", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Navy)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Free: Core Concepts and Phase I (Business Understanding), including those notes, cards, and quizzes.\n\n" +
                        "Full (${Entitlement.priceLabel}): Phases II–VI, 7 Patterns, exam simulator, daily drill across the whole syllabus, and all flashcards.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "This sideloaded app cannot use Google Play Billing yet. After you pay, you receive a license code like PREP-AB12CD-E9F0. Enter it below.",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("License code") },
                    placeholder = { Text("PREP-XXXXXX-XXXX") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (LicenseKeys.isValid(code)) {
                            store.unlockFull()
                            ok = true
                            message = "Unlocked. Thank you."
                        } else {
                            message = "That code is not valid. Check spaces and dashes."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Unlock for ${Entitlement.priceLabel}") }
                message?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
