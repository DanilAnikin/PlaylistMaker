package com.example.playlistmaker.search.domain.api

interface TracksRepository {
    fun loadTracks(query: String)
    fun cancelLoading()
    var tracksLoadResultListener: TracksLoadResultListener?
}