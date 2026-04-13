package com.example.playlistmaker.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class SettingsViewModelFactory(
    private val themeInteractor: ThemeInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themeInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}