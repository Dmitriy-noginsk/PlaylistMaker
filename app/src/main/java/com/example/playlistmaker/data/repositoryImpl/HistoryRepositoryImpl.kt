package com.example.playlistmaker.data.repositoryImpl

import com.example.playlistmaker.data.storage.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val storage: SearchHistory
) : HistoryRepository {
    override fun getHistory(): List<Track> = storage.get()
    override fun addToHistory(track: Track) = storage.add(track)
    override fun clearHistory() = storage.clear()
    override fun isNotEmpty(): Boolean = storage.isNotEmpty()
}
