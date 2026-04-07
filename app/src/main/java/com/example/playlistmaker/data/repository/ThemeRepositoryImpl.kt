package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, isDark)
            .apply()
    }
}