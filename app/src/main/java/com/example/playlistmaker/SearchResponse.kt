package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val resultCount: Int?,
    val results: List<TrackDto>?
)

data class TrackDto(
    @SerializedName("trackId") val trackId: Long?,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("primaryGenreName") val primaryGenreName: String?,
    @SerializedName("country") val country: String?
)

fun TrackDto.toDomain(): Track = Track(
    trackId = trackId ?: 0L,
    trackName = trackName.orEmpty(),
    artistName = artistName.orEmpty(),
    trackTimeMillis = trackTimeMillis ?: 0L,
    artworkUrl100 = artworkUrl100.orEmpty(),
    collectionName = collectionName.orEmpty(),
    releaseDate = releaseDate.orEmpty(),
    primaryGenreName = primaryGenreName.orEmpty(),
    country = country.orEmpty()
)

