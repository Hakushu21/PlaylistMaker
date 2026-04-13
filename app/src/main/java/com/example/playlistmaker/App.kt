package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.getKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val themeInteractor: ThemeInteractor = getKoin().get()
        switchTheme(themeInteractor.isDarkTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val themeInteractor: ThemeInteractor = getKoin().get()
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