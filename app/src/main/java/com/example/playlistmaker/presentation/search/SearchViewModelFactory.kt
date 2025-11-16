package com.example.playlistmaker.presentation.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.Creator

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tracksInteractor = Creator.provideTracksInteractor()
        val historyInteractor = Creator.provideHistoryInteractor(context)

        return SearchViewModel(
            tracksInteractor,
            historyInteractor
        ) as T
    }
}