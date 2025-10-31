package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun get(): List<Track>
    fun add(track: Track)
    fun clear()
    fun isNotEmpty(): Boolean
}