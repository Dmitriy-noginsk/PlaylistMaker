package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

fun TrackDto.toDomain(): Track = Track(
    trackId = trackId ?: 0L,
    trackName = trackName.orEmpty(),
    artistName = artistName.orEmpty(),
    trackTimeMillis = trackTimeMillis ?: 0L,
    artworkUrl100 = artworkUrl100.orEmpty(),
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName.orEmpty(),
    country = country.orEmpty(),
    previewUrl = previewUrl
)