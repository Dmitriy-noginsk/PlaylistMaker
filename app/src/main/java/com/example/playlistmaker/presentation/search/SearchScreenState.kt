package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.models.Track

data class SearchScreenState(
    val query: String = "",
    val searchState: SearchState = SearchState.Idle,
    val history: List<Track> = emptyList()
)
