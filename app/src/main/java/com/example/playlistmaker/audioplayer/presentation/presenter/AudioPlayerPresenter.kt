package com.example.playlistmaker.audioplayer.presentation.presenter

import com.example.playlistmaker.audioplayer.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.audioplayer.domain.enums.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.audioplayer.presentation.AudioPlayerView
import com.example.playlistmaker.audioplayer.presentation.PlaybackTimer

class AudioPlayerPresenter(
    private val view: AudioPlayerView,
    private val interactor: AudioPlayerInteractor,
    private val playbackTimer: PlaybackTimer
) {

    private var playerState: PlayerState = PlayerState.DEFAULT

    init {
        interactor.subscribeOnPlayer { state ->
            playerState = state
            if (state == PlayerState.COMPLETE) {
                playbackTimer.stopUpdate()
                view.setPlayIcon()
                view.updatePlaybackTime(PLAYBACK_TIME_START)
            }
        }

        playbackTimer.onTimeUpdate = {
            val currentTime = interactor.getCurrentPlaybackTime()
            view.updatePlaybackTime(currentTime)
        }
    }

    private fun playTrack() {
        playbackTimer.startUpdate()
        interactor.playTrack()
        view.setPauseIcon()
    }

    private fun pauseTrack() {
        playbackTimer.stopUpdate()
        interactor.pauseTrack()
        view.setPlayIcon()
    }

    fun playbackControl() {
        when (playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.COMPLETE -> {
                playTrack()
            }
            PlayerState.PLAYING -> {
                pauseTrack()
            }
            PlayerState.DEFAULT -> Unit
        }
    }

    fun drawPlayer(track: Track) {
        view.drawPlayer(track)
    }

    fun showReleaseDateIfNeeded(releaseDate: String?) {
        if (releaseDate != null) {
            view.showReleaseDate(releaseDate.take(CHARACTERS_AMOUNT_FOR_YEAR))
        } else {
            view.hideReleaseDate()
        }
    }

    fun onBackButtonClicked() {
        view.goBack()
    }

    fun onViewDestroyed() {
        interactor.unsubscribeFromPlayer()
        playbackTimer.stopUpdate()
    }

    fun onViewHidden() {
        pauseTrack()
    }

    companion object {
        const val PLAYBACK_TIME_START = 0
        const val CHARACTERS_AMOUNT_FOR_YEAR = 4
    }
}