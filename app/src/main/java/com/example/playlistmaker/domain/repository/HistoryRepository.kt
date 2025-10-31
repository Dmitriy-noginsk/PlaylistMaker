package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
    fun isNotEmpty(): Boolean
}