package com.example.playlistmaker.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.SearchScreenState.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private var searchJob: Job? = null
    private var latestQuery: String? = null

    private val _screenState = MutableLiveData<SearchScreenState>()
    val screenState: LiveData<SearchScreenState> = _screenState

    init {
        _screenState.value = SearchScreenState()
        showHistoryIfAvailable()
    }

    fun onQueryTextChanged(query: String) {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(queryText = query)

        searchJob?.cancel()

        if (query.isEmpty()) {
            showHistoryIfAvailable()
            return
        }

        searchJob = viewModelScope.launch {
            delay(2000L)
            performSearch(query)
        }
    }

    fun onSearchAction() {
        val query = _screenState.value?.queryText ?: ""
        if (query.isNotBlank()) {
            searchJob?.cancel()
            performSearch(query)
        }
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            searchInteractor.addTrackToHistory(track)
            val currentState = _screenState.value ?: return@launch
            _screenState.value = currentState.copy(navigateToPlayer = track)
        }
    }

    fun onClearHistoryClicked() {
        searchInteractor.clearSearchHistory()
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(searchState = SearchState.StartSearch)
    }

    fun onNavigateToPlayerHandled() {
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(navigateToPlayer = null)
    }

    fun onRetryClicked() {
        latestQuery?.let { query ->
            performSearch(query)
        }
    }

    fun onSearchFieldFocused(hasFocus: Boolean) {
        val query = _screenState.value?.queryText ?: ""
        if (hasFocus && query.isEmpty()) {
            showHistoryIfAvailable()
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        latestQuery = query
        val currentState = _screenState.value ?: return
        _screenState.value = currentState.copy(searchState = SearchState.Loading)

        viewModelScope.launch {
            val result = searchInteractor.searchTracks(query)
            result.onSuccess { tracks ->
                val newState = _screenState.value ?: return@onSuccess
                if (tracks.isNotEmpty()) {
                    _screenState.value = newState.copy(
                        searchState = SearchState.Content(tracks)
                    )
                } else {
                    _screenState.value = newState.copy(
                        searchState = SearchState.Empty
                    )
                }
            }.onFailure {
                val newState = _screenState.value ?: return@onFailure
                _screenState.value = newState.copy(
                    searchState = SearchState.Error
                )
            }
        }
    }

    private fun showHistoryIfAvailable() {
        val history = searchInteractor.getSearchHistory()
        val currentState = _screenState.value ?: return
        if (history.isNotEmpty()) {
            _screenState.value = currentState.copy(searchState = SearchState.History(history))
        } else {
            _screenState.value = currentState.copy(searchState = SearchState.StartSearch)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}