package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    var trackTimeMillis: String?,
    val artworkUrl100: String,
    val collectionName: String, // album name
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
) : Parcelable {
    fun getCoverArtworkUrl() = artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
}
