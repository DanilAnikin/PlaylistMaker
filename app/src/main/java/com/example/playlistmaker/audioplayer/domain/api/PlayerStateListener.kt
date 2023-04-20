package com.example.playlistmaker.audioplayer.domain.api

import com.example.playlistmaker.audioplayer.domain.enums.PlayerState

fun interface PlayerStateListener {
    fun onStateChanged(state: PlayerState)
}