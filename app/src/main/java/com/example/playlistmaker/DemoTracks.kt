package com.example.playlistmaker

object DemoTracks {
    val tracks = listOf(
        Track(
            trackId = 1L,
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            collectionName = "Nevermind",
            releaseDate = "1991",
            primaryGenreName = "Rock",
            country = "USA",
            trackTimeMillis = 301_000L,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackId = 2L,
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            collectionName = "Thriller",
            releaseDate = "1982",
            primaryGenreName = "Pop",
            country = "USA",
            trackTimeMillis = 294_000L,
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            trackId = 3L,
            trackName = "Stairway to Heaven",
            artistName = "Led Zeppelin",
            collectionName = "Led Zeppelin IV",
            releaseDate = "1971",
            primaryGenreName = "Rock",
            country = "UK",
            trackTimeMillis = 482_000L,
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/4f/bb/3b/4fbb3bb3-2ec3-1959-cdb2-cf041812cdad/081227965915.jpg/100x100bb.jpg"
        )
    )
}
