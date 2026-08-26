package com.example.nomadcompass.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import com.example.nomadcompass.ui.navigation.NomadCompassNavGraph
import com.example.nomadcompass.ui.theme.NomadCompassTheme
import com.example.nomadcompass.ui.theme.ThemeController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeController = remember { ThemeController(initialDark = true) }

            LaunchedEffect(Unit) {
                val profile = getProfileUseCase().firstOrNull()
                if (profile != null) {
                    val isDark = profile.themeMode != "LIGHT"
                    themeController.updateTheme(isDark)
                }
            }

            NomadCompassTheme(themeController = themeController) {
                NomadCompassNavGraph()
            }
        }
    }
}
