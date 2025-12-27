package com.mujapps.mchatbot.presentation.navigation

enum class ChatBotAppNavigation {
    HomeScreen,
    SettingsScreen;

    companion object Companion {
        fun fromRoute(route: String): ChatBotAppNavigation = when (route.substringBefore("/")) {
            HomeScreen.name -> HomeScreen
            SettingsScreen.name -> SettingsScreen
            else -> {
                throw IllegalArgumentException("Route Exception")
            }
        }
    }
}