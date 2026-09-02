package com.cpmai.study.ui.screens

import android.app.Activity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.BuildConfig
import com.cpmai.study.CpmaiApplication
import com.cpmai.study.data.Entitlement
import com.cpmai.study.data.LicenseKeys
import com.cpmai.study.data.ProgressStore
import com.cpmai.study.data.UserProgress
import com.cpmai.study.ui.theme.Navy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockScreen(store: ProgressStore, progress: UserProgress, onBack: () -> Unit) {
    val context = LocalContext.current
    val billing = (context.applicationContext as CpmaiApplication).billing
    val playPrice by billing.priceLabel.collectAsState()
    val playMessage by billing.message.collectAsState()
    val unlocked = progress.fullUnlocked
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val usePlay = BuildConfig.USE_PLAY_BILLING

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
            if (unlocked) {
                Text("Full version unlocked", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Navy)
                Spacer(Modifier.height(8.dp))
                Text("All modules, the exam simulator, and the full card deck are available on this device.")
                if (usePlay) {
                    Spacer(Modifier.height(8.dp))
                    Text("Purchases restore automatically from Google Play on this account.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Free vs full", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Navy)
                Spacer(Modifier.height(8.dp))
                val price = if (usePlay) playPrice else Entitlement.priceLabel
                Text(
                    "Free: Core Concepts and Phase I (Business Understanding), including those notes, cards, and quizzes.\n\n" +
                        "Full ($price): Phases II–VI, 7 Patterns, exam simulator, daily drill across the whole syllabus, and all flashcards.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                if (usePlay) {
                    Text(
                        "Payment is handled by Google Play. Create the in-app product \"${com.cpmai.study.data.PlayBilling.PRODUCT_ID}\" in Play Console. License testers can buy without being charged.",
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val activity = context as? Activity
                            if (activity == null) {
                                message = "Open this screen from the app window."
                            } else {
                                billing.buy(activity)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Unlock with Google Play · $price") }
                    playMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                    message?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                } else {
                    Text(
                        "This sideloaded build uses a license code. After you pay outside the store, enter PREP-XXXXXX-XXXX below. The Play Store build uses Google Play Billing instead.",
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
                                message = null
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
}
