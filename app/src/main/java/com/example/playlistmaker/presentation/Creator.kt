package com.example.playlistmaker.presentation

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.PlayerInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.domain.interactor.ThemeInteractorImpl
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.presentation.ui.player.PlayerViewModelFactory
import com.example.playlistmaker.presentation.ui.search.SearchViewModelFactory
import com.example.playlistmaker.presentation.ui.settings.SettingsViewModelFactory

object Creator {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val searchHistoryStorage: SearchHistoryStorage by lazy {
        SearchHistoryStorage(sharedPreferences)
    }

    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(searchHistoryStorage)
    }

    private val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(sharedPreferences)
    }

    private val playerRepository: PlayerRepository by lazy {
        MediaPlayerRepositoryImpl()
    }

    private val searchInteractor: SearchInteractor by lazy {
        SearchInteractorImpl(trackRepository)
    }

    private val themeInteractor: ThemeInteractor by lazy {
        ThemeInteractorImpl(themeRepository)
    }

    private val playerInteractor: PlayerInteractor by lazy {
        PlayerInteractorImpl(playerRepository)
    }

    fun provideSearchInteractor(): SearchInteractor = searchInteractor

    fun provideThemeInteractor(): ThemeInteractor = themeInteractor

    fun provideSearchViewModelFactory(): SearchViewModelFactory {
        return SearchViewModelFactory(searchInteractor)
    }

    fun providePlayerViewModelFactory(): PlayerViewModelFactory {
        return PlayerViewModelFactory(playerInteractor, searchInteractor)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        return SettingsViewModelFactory(themeInteractor)
    }
}