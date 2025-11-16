package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class HistoryInteractorImpl(
    private val repository: HistoryRepository
) : HistoryInteractor {

    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addToHistory(track: Track) {
        repository.addToHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }

    override fun isNotEmpty(): Boolean = repository.isNotEmpty()
}
