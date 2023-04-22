package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
    fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit)
    fun cancelLoadingTracks()
    fun addTrackToHistory(track: Track)
}