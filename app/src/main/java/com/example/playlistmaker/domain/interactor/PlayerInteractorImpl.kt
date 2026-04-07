package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.player.MediaPlayerRepository

class PlayerInteractorImpl(
    private val repository: MediaPlayerRepository
) : PlayerInteractor {

    override fun initializePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit) {
        repository.initializePlayer(url, onPrepared, onCompletion, onError)
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun isPlaying(): Boolean = repository.isPlaying()

    override fun getCurrentPosition(): Int = repository.getCurrentPosition()

    override fun releasePlayer() {
        repository.releasePlayer()
    }
}