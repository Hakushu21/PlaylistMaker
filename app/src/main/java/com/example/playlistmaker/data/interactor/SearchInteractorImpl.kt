package com.example.playlistmaker.data.interactor

import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.models.Track

class SearchInteractorImpl(
    private val repository: TrackRepositoryImpl,
    private val searchHistoryStorage: SearchHistoryStorage
) : SearchInteractor {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return repository.searchTracks(query)
    }

    override fun getSearchHistory(): List<Track> {
        return repository.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrack(track)
    }

    override fun clearSearchHistory() {
        repository.clearSearchHistory()
    }
}