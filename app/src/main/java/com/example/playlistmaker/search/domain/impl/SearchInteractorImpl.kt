package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryStorage
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchInteractorImpl(
    private val searchHistoryStorage: SearchHistoryStorage,
    private val repository: TracksRepository
) : SearchInteractor {

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getTracks()
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clear()
    }

    override fun loadTracks(query: String, onSuccess: (List<Track>) -> Unit, onError: () -> Unit) {
        repository.loadTracks(query, onSuccess, onError)
    }

    override fun cancelLoadingTracks() {
        repository.cancelLoading()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrack(track)
    }
}