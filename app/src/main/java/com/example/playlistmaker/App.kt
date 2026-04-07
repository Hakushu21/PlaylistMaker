package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val themeInteractor = Creator.provideThemeInteractor()
        switchTheme(themeInteractor.isDarkTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val themeInteractor = Creator.provideThemeInteractor()
        themeInteractor.setDarkTheme(darkThemeEnabled)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}