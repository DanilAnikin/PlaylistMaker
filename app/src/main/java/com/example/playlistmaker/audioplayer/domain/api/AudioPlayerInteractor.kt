package com.example.playlistmaker.audioplayer.domain.api

interface AudioPlayerInteractor {
    fun playTrack()
    fun pauseTrack()
    fun subscribeOnPlayer(listener: PlayerStateListener)
    fun unsubscribeFromPlayer()
    fun getCurrentPlaybackTime(): Int
}