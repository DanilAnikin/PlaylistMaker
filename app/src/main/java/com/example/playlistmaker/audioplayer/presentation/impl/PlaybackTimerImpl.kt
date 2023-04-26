package com.example.playlistmaker.audioplayer.presentation.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.audioplayer.presentation.PlaybackTimer

class PlaybackTimerImpl: PlaybackTimer {

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlaybackRunnable = object : Runnable {
        override fun run() {
            onTimeUpdate?.invoke()
            handler.postDelayed(this, PLAYBACK_TIME_UPDATE_DELAY)
        }
    }

    override var onTimeUpdate: (() -> Unit)? = null

    override fun startUpdate() {
        handler.post(updatePlaybackRunnable)
    }

    override fun stopUpdate() {
        handler.removeCallbacks(updatePlaybackRunnable)
    }

    companion object {
        const val PLAYBACK_TIME_UPDATE_DELAY = 500L
    }
}