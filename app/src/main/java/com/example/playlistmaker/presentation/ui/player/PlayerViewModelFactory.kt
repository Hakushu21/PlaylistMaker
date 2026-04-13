package com.example.playlistmaker.presentation.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor

class PlayerViewModelFactory(
    private val playerInteractor: PlayerInteractor,
    private val searchInteractor: SearchInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(playerInteractor, searchInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}