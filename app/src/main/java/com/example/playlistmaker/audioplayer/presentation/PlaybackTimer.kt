package com.example.playlistmaker.audioplayer.presentation

interface PlaybackTimer {
    fun startUpdate()
    fun stopUpdate()
    var onTimeUpdate: (() -> Unit)?
}