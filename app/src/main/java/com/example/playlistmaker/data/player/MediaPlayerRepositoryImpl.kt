package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.PlayerRepository

class MediaPlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun initializePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit, onError: () -> Unit) {
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

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun releasePlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}