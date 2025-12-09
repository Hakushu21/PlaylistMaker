package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    companion object {
        const val THEME_PREFS_NAME = "theme_prefs"
        const val DARK_THEME_KEY = "dark_theme"
    }

    override fun onCreate() {
        super.onCreate()

        // Загружаем сохраненную тему из SharedPreferences
        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)

        // Устанавливаем тему при запуске приложения
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        // Сохраняем настройку в SharedPreferences
        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()

        // Устанавливаем тему
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}