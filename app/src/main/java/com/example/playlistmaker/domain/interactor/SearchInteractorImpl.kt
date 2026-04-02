package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class SearchInteractorImpl(
    private val repository: TrackRepository
) : SearchInteractor {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return repository.searchTracks(query)
    }

    override fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}