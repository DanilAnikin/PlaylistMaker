package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryStorage {
    fun addTrack(track: Track)
    fun clear()
    fun getTracks(): List<Track>
}