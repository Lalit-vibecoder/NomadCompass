package com.example.nomadcompass.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nomadcompass.ui.navigation.NomadCompassNavGraph
import com.example.nomadcompass.ui.theme.NomadCompassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NomadCompassTheme {
                NomadCompassNavGraph()
            }
        }
    }
}
