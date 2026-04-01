package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
    fun saveSearchHistory(tracks: List<Track>)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun addTrackToHistory(track: Track)
}