package com.example.playlistmaker.presentation.ui.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var progressUpdateRunnable: Runnable? = null

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> = _screenState

    fun initTrack(track: Track) {
        _screenState.value = PlayerScreenState(track = track)

        track.previewUrl?.let { url ->
            playerInteractor.initializePlayer(
                url = url,
                onPrepared = {
                    val currentState = _screenState.value ?: return@initializePlayer
                    _screenState.postValue(currentState.copy(isPrepared = true))
                },
                onCompletion = {
                    onPlaybackComplete()
                },
                onError = {
                    val currentState = _screenState.value ?: return@initializePlayer
                    _screenState.postValue(currentState.copy(isPrepared = false))
                }
            )
        } ?: run {
            val currentState = _screenState.value ?: return
            _screenState.value = currentState.copy(isPrepared = false)
        }
    }

    fun togglePlayPause() {
        val currentState = _screenState.value ?: return
        if (!currentState.isPrepared) return

        if (currentState.isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        playerInteractor.start()
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(isPlaying = true)
        startProgressUpdate()
    }

    private fun pausePlayback() {
        playerInteractor.pause()
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(isPlaying = false)
        stopProgressUpdate()
    }

    private fun onPlaybackComplete() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(isPlaying = false, currentPosition = 0)
        stopProgressUpdate()
    }

    private fun startProgressUpdate() {
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                if (playerInteractor.isPlaying()) {
                    val currentPosition = playerInteractor.getCurrentPosition()
                    val currentState = _screenState.value ?: return
                    _screenState.value = currentState.copy(currentPosition = currentPosition)
                    handler.postDelayed(this, 500)
                }
            }
        }
        handler.post(progressUpdateRunnable!!)
    }

    private fun stopProgressUpdate() {
        progressUpdateRunnable?.let { handler.removeCallbacks(it) }
        progressUpdateRunnable = null
    }

    fun onBackPressed() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateBack = true)
    }

    fun onNavigateBackHandled() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateBack = false)
    }

    fun onAddToPlaylistClicked() {
    }

    fun onFavoriteClicked() {
        val currentState = _screenState.value ?: return
        currentState.track?.let { track ->
            viewModelScope.launch {
                searchInteractor.addTrackToHistory(track)
            }
        }
    }

    fun onPause() {
        if (_screenState.value?.isPlaying == true) {
            pausePlayback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdate()
        playerInteractor.releasePlayer()
    }
}