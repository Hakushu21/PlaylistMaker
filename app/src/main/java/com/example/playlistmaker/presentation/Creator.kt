package com.example.playlistmaker.presentation

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.interactor.SearchInteractorImpl
import com.example.playlistmaker.data.interactor.ThemeInteractorImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.ThemeInteractor
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.repository.ThemeRepository

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

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(trackRepository)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(themeRepository)
    }
}