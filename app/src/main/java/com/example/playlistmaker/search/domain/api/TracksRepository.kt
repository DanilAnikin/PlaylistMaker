package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit)
    fun cancelLoading()
}