package com.example.playlistmaker.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactor.SearchInteractor

class SearchViewModelFactory(
    private val searchInteractor: SearchInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}