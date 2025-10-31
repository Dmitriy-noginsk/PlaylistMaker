package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}