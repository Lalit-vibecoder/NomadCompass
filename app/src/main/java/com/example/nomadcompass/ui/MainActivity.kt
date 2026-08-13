package com.example.nomadcompass.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.nomadcompass.ui.navigation.NomadCompassNavGraph
import com.example.nomadcompass.ui.theme.NomadCompassTheme
import com.example.nomadcompass.ui.theme.ThemeController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeController = remember { ThemeController(initialDark = true) }
            NomadCompassTheme(themeController = themeController) {
                NomadCompassNavGraph()
            }
        }
    }
}
