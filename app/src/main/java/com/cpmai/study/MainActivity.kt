package com.cpmai.study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cpmai.study.ui.CpmaiRoot
import com.cpmai.study.ui.theme.CpmaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as CpmaiApplication
        setContent {
            CpmaiTheme {
                CpmaiRoot(app.repository, app.progress)
            }
        }
    }
}
