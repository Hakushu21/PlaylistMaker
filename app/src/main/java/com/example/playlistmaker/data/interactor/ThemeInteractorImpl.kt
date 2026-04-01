package com.example.playlistmaker.data.interactor

import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.domain.interactor.ThemeInteractor

class ThemeInteractorImpl(
    private val repository: ThemeRepositoryImpl
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean {
        return repository.isDarkTheme()
    }

    override fun setDarkTheme(isDark: Boolean) {
        repository.setDarkTheme(isDark)
    }
}