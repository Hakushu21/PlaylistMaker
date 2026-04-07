package com.example.playlistmaker.presentation.ui.settings

data class SettingsScreenState(
    val isDarkTheme: Boolean = false,
    val navigateToShare: Boolean = false,
    val navigateToSupport: Boolean = false,
    val navigateToAgreement: Boolean = false
)