package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryStorage
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.TracksLoadResultListener
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchInteractorImpl(
    private val searchHistoryStorage: SearchHistoryStorage,
    private val repository: TracksRepository
) : SearchInteractor {

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrack(track)
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getTracks()
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clear()
    }

    override fun subscribeOnTracksLoadResult(listener: TracksLoadResultListener) {
        repository.tracksLoadResultListener = listener
    }

    override fun unsubscribeFromTracksLoadResult() {
        repository.tracksLoadResultListener = null
    }

    override fun loadTracks(query: String) {
        repository.loadTracks(query)
    }

    override fun cancelLoadingTracks() {
        repository.cancelLoading()
    }
}