package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    suspend fun search(query: String): Result<List<Track>>
}