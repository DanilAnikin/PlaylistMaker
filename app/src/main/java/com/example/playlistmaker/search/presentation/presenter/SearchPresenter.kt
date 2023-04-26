package com.example.playlistmaker.search.presentation.presenter

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.TracksLoadResultListener
import com.example.playlistmaker.search.presentation.SearchScreenView

class SearchPresenter(
    private val view: SearchScreenView,
    private val interactor: SearchInteractor
) {

    init {
        interactor.subscribeOnTracksLoadResult(object : TracksLoadResultListener {
            override fun onSuccess(tracks: List<Track>) {
                if (tracks.isEmpty()) {
                    view.showNothingFoundPlaceholder()
                } else {
                    view.showTrackList(tracks)
                }
            }

            override fun onError() {
                view.showConnectionErrorPlaceholder()
            }
        })

        showTracksHistory()
    }

    fun clearHistoryButtonClicked() {
        interactor.clearSearchHistory()
        view.hideHistory()
    }

    fun searchFieldFocusChanged(hasFocus: Boolean, searchText: CharSequence) {
        if (hasFocus && searchText.isEmpty()) {
            showTracksHistory()
        } else {
            view.hideHistory()
        }
    }

    fun findTracks(query: String) {
        if (query.isEmpty()) return

        view.onLoading()

        interactor.loadTracks(query)
    }

    fun searchFieldClearButtonClicked() {
        interactor.cancelLoadingTracks()
        view.clearSearchField()
        view.hidePlaceholder()
        view.hideKeyboard()
        view.hideTrackList()
    }

    fun backButtonClicked() {
        view.goBack()
    }

    fun searchFieldTextChanged(hasFocus: Boolean, text: CharSequence?) {
        view.setClearButtonVisibility()

        if (hasFocus && text?.isEmpty() == true) {
            showTracksHistory()
        } else {
            view.hideHistory()
        }

        view.searchDebounce()
    }

    private fun showTracksHistory() {
        val tracksHistory = interactor.getSearchHistory()
        if (tracksHistory.isNotEmpty()) {
            view.showHistory(tracksHistory)
        }
    }

    fun trackClicked(track: Track) {
        interactor.addTrackToHistory(track)
        view.openAudioPlayerScreen(track)
    }

    fun historyTrackClicked(track: Track) {
        view.openAudioPlayerScreen(track)
    }

    fun onViewDestroyed() {
        interactor.unsubscribeFromTracksLoadResult()
    }
}