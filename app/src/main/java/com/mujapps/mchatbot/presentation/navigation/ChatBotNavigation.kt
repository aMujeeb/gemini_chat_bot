package com.mujapps.mchatbot.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mujapps.mchatbot.presentation.screens.HomeScreen
import com.mujapps.mchatbot.presentation.screens.SettingsScreen
import com.mujapps.mchatbot.presentation.views.MainViewModel

@Composable
fun ChatBotNavigation(mMainViewModel: MainViewModel) {
    val mNavController = rememberNavController()

    NavHost(mNavController, startDestination = ChatBotAppNavigation.HomeScreen.name) {
        composable(ChatBotAppNavigation.HomeScreen.name) {
            HomeScreen(mMainViewModel = mMainViewModel) {
                mNavController.navigate(ChatBotAppNavigation.SettingsScreen.name) {
                    popUpTo(0) {
                        inclusive = false
                    }
                }
            }
        }

        composable(ChatBotAppNavigation.SettingsScreen.name) {
            SettingsScreen()
        }
    }
}