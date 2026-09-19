package com.neverdid.outside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.neverdid.outside.data.AppContainer
import com.neverdid.outside.ui.theme.OutsideTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy { AppContainer.create(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OutsideTheme {
                OutsideRoot(container = appContainer)
            }
        }
    }
}
