package com.example.playlistmaker.presentation.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<SettingsScreenState>()
    val screenState: LiveData<SettingsScreenState> = _screenState

    init {
        _screenState.value = SettingsScreenState(
            isDarkTheme = themeInteractor.isDarkTheme()
        )
    }

    fun onThemeSwitched(isDark: Boolean) {
        themeInteractor.setDarkTheme(isDark)
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(isDarkTheme = isDark)
    }

    fun onShareAppClicked() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateToShare = true)
    }

    fun onSupportClicked() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateToSupport = true)
    }

    fun onUserAgreementClicked() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateToAgreement = true)
    }

    fun onNavigateHandled(type: String) {
        val currentState = _screenState.value ?: return
        _screenState.value = when (type) {
            "share" -> currentState.copy(navigateToShare = false)
            "support" -> currentState.copy(navigateToSupport = false)
            "agreement" -> currentState.copy(navigateToAgreement = false)
            else -> currentState
        }
    }
}