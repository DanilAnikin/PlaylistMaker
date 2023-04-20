package com.example.playlistmaker.audioplayer.presentation

import com.example.playlistmaker.domain.models.Track

interface AudioPlayerView {
    fun drawPlayer(track: Track)
    fun setPlayIcon()
    fun setPauseIcon()
    fun updatePlaybackTime(currentTime: Int)
    fun showReleaseDate(releaseDate: String)
    fun hideReleaseDate()
    fun goBack()
}