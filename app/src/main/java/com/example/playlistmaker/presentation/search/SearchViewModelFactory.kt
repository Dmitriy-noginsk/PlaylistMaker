package com.example.playlistmaker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.interactor.TracksInteractor

class SearchViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val interactor = Creator.provideTracksInteractor()
        return SearchViewModel(interactor) as T
    }
}