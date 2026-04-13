package com.example.playlistmaker.presentation.ui.search

import com.example.playlistmaker.domain.models.Track

data class SearchScreenState(
    val searchState: SearchState = SearchState.StartSearch,
    val queryText: String = "",
    val navigateToPlayer: Track? = null
) {
    sealed class SearchState {
        data object Loading : SearchState()
        data class Content(val tracks: List<Track>) : SearchState()
        data object Empty : SearchState()
        data object Error : SearchState()
        data class History(val tracks: List<Track>) : SearchState()
        data object StartSearch : SearchState()
    }
}