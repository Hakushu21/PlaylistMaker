package com.example.playlistmaker.presentation.ui.player

import com.example.playlistmaker.domain.models.Track

data class PlayerScreenState(
    val track: Track? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val isPrepared: Boolean = false,
    val navigateBack: Boolean = false
)