package com.example.playlistmaker

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Content(val items: List<Track>) : SearchState
    data object Empty : SearchState
    data class Error(val lastQuery: String) : SearchState
}
