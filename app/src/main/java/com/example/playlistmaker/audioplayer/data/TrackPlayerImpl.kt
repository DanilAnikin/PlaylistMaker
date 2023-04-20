package com.example.playlistmaker.audioplayer.data

import android.media.MediaPlayer
import com.example.playlistmaker.audioplayer.domain.api.PlayerStateListener
import com.example.playlistmaker.audioplayer.domain.api.TrackPlayer
import com.example.playlistmaker.audioplayer.domain.enums.PlayerState

class TrackPlayerImpl(private val previewUrl: String) : TrackPlayer {

    private val player = MediaPlayer()
    override var listener: PlayerStateListener? = null

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        player.setDataSource(previewUrl)
        player.prepareAsync()

        player.setOnPreparedListener { listener?.onStateChanged(PlayerState.PREPARED) }

        player.setOnCompletionListener {
            listener?.onStateChanged(PlayerState.COMPLETE)
            player.seekTo(PLAYER_START_POSITION)
        }
    }

    override fun play() {
        listener?.onStateChanged(PlayerState.PLAYING)
        player.start()
    }

    override fun pause() {
        listener?.onStateChanged(PlayerState.PAUSED)
        player.pause()
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun getCurrentPlaybackTime() = player.currentPosition

    companion object {
        const val PLAYER_START_POSITION = 0
    }
}