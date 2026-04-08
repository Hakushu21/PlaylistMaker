package com.example.playlistmaker.domain.repository

interface PlayerRepository {
    fun initializePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit)
    fun start()
    fun pause()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun releasePlayer()
}