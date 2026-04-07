package com.example.playlistmaker.data.player

import android.media.MediaPlayer

class MediaPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    fun initializePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit) {
        releasePlayer()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    onPrepared()
                }

                setOnCompletionListener {
                    onCompletion()
                }

                setOnErrorListener { _, _, _ ->
                    onError()
                    false
                }
            }
        } catch (e: Exception) {
            onError()
        }
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun releasePlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}