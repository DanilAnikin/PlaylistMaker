package com.example.playlistmaker.audioplayer.domain.api

interface TrackPlayer {
    fun play()
    fun pause()
    fun releasePlayer()
    fun getCurrentPlaybackTime(): Int
    var listener: PlayerStateListener?
}