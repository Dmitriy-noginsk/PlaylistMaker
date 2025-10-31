package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Content(val items: List<Track>) : SearchState
    data object Empty : SearchState
    data class Error(val lastQuery: String) : SearchState
}