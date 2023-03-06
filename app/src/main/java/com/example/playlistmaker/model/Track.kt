package com.example.playlistmaker.model

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    var trackTimeMillis: String,
    val artworkUrl100: String
)
