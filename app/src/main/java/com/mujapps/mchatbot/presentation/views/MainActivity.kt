package com.mujapps.mchatbot.presentation.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mujapps.mchatbot.presentation.navigation.ChatBotNavigation
import com.mujapps.mchatbot.ui.theme.MChatBotTheme

class MainActivity : ComponentActivity() {
    private val mMainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MChatBotTheme {
                ChatBotNavigation(mMainViewModel)
            }
        }
    }
}
