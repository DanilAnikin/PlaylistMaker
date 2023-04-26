package com.example.playlistmaker.audioplayer.domain.impl

import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.api.PlayerStateListener
import com.example.playlistmaker.audioplayer.domain.api.TrackPlayer

class AudioPlayerInteractorImpl(
    private val trackPlayer: TrackPlayer
) : AudioPlayerInteractor {

    override fun playTrack() {
        trackPlayer.play()
    }

    override fun pauseTrack() {
        trackPlayer.pause()
    }

    override fun subscribeOnPlayer(listener: PlayerStateListener) {
        trackPlayer.listener = listener
    }

    override fun unsubscribeFromPlayer() {
        trackPlayer.listener = null
        trackPlayer.releasePlayer()
    }

    override fun getCurrentPlaybackTime() = trackPlayer.getCurrentPlaybackTime()
}