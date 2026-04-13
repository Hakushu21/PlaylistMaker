package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.player.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<SearchHistoryStorage> {
        SearchHistoryStorage(get(), get())
    }

    single<PlayerRepository> {
        MediaPlayerRepositoryImpl()
    }
}