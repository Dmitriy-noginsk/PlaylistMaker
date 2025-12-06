package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
    fun isNotEmpty(): Boolean
}
