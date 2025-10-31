package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.TracksRepository

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {
    override suspend fun search(query: String) = repository.searchTracks(query)
}