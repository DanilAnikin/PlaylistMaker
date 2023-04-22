package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.search.domain.models.Track

interface SearchScreenView {
    fun showHistory(tracksHistory: List<Track>)
    fun hideHistory()
    fun showKeyboard()
    fun hideKeyboard()
    fun showTrackList(tracks: List<Track>)
    fun hideTrackList()
    fun showNothingFoundPlaceholder()
    fun showConnectionErrorPlaceholder()
    fun hidePlaceholder()
    fun clearSearchField()
    fun onLoading()
    fun goBack()
    fun setClearButtonVisibility()
    fun searchDebounce()
    fun openAudioPlayerScreen(track: Track)
}