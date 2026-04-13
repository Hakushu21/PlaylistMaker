package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
}