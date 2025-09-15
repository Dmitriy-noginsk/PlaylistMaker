package com.example.playlistmaker

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Content(val items: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val lastQuery: String) : SearchState()
}
