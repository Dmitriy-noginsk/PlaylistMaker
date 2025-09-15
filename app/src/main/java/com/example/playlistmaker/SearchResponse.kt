package com.example.playlistmaker

data class SearchResponse(
    val resultCount: Int?,
    val results: List<TrackDto>?
)

data class TrackDto(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?
)

fun TrackDto.toDomain(): Track = Track(
    trackName = trackName.orEmpty(),
    artistName = artistName.orEmpty(),
    trackTimeMillis = trackTimeMillis ?: 0L,
    artworkUrl100 = artworkUrl100
)
