package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(
    private val repo: HistoryRepository
) : HistoryInteractor {
    override fun get() = repo.getHistory()
    override fun add(track: Track) = repo.addToHistory(track)
    override fun clear() = repo.clearHistory()
    override fun isNotEmpty() = repo.isNotEmpty()
}