package com.cpmai.study.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cpmai.study.R
import com.cpmai.study.ui.theme.Navy
import com.cpmai.study.ui.theme.Saffron

@Composable
fun DisclaimerGate(onAccept: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Navy)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("UNOFFICIAL", color = Saffron, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.disclaimer_title), color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.disclaimer_body), color = androidx.compose.ui.graphics.Color(0xFFD5DEEE), fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(28.dp))
        Button(onClick = onAccept, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.disclaimer_accept))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Legal") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Not affiliated with PMI", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.disclaimer_body), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.legal_attribution), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
