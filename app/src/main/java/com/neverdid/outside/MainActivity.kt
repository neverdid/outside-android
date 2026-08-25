package com.neverdid.outside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.neverdid.outside.data.session.LocalSessionRepository
import com.neverdid.outside.ui.theme.OutsideTheme

class MainActivity : ComponentActivity() {
    private val sessionRepository by lazy { LocalSessionRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OutsideTheme {
                OutsideRoot(repository = sessionRepository)
            }
        }
    }
}
