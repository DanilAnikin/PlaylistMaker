package com.example.playlistmaker

sealed interface PlayerState {
    object Default: PlayerState
    object Prepared: PlayerState
    object Playing: PlayerState
    object Paused: PlayerState
}