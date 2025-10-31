package com.example.playlistmaker.data.repositoryImpl

import com.example.playlistmaker.data.dto.toDomain
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.data.network.ItunesApi

class TracksRepositoryImpl(
    private val api: ItunesApi
) : TracksRepository {
    override suspend fun searchTracks(query: String): Result<List<Track>> = try {
        val resp = api.search(query)
        if (resp.isSuccessful) {
            val list = resp.body()?.results.orEmpty().map { it.toDomain() }
            Result.success(list)
        } else {
            Result.failure(IllegalStateException("HTTP ${resp.code()}"))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }
}