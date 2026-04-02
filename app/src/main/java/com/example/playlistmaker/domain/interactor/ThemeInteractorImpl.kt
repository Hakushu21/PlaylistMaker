package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeInteractorImpl(
    private val repository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean {
        return repository.isDarkTheme()
    }

    override fun setDarkTheme(isDark: Boolean) {
        repository.setDarkTheme(isDark)
    }
}