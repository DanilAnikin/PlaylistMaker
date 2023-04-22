package com.example.playlistmaker.audioplayer.creator

import com.example.playlistmaker.audioplayer.data.TrackPlayerImpl
import com.example.playlistmaker.audioplayer.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.audioplayer.presentation.presenter.AudioPlayerPresenter
import com.example.playlistmaker.audioplayer.presentation.AudioPlayerView
import com.example.playlistmaker.audioplayer.presentation.impl.PlaybackTimerImpl

object Creator {

    fun providePresenter(view: AudioPlayerView, track: Track): AudioPlayerPresenter {
        val interactor = AudioPlayerInteractorImpl(
            trackPlayer = TrackPlayerImpl(previewUrl = track.previewUrl!!)
        )

        return AudioPlayerPresenter(
            view = view,
            interactor = interactor,
            playbackTimer = PlaybackTimerImpl()
        )
    }
}