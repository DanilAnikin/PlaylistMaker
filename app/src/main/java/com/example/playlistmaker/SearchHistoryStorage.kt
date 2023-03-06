package com.example.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(private val sharedPrefs: SharedPreferences) {
    private val gson: Gson = Gson()
    private val typeToken = object : TypeToken<List<Track>>() {}.type

    fun addTrack(track: Track) {
        val tracks = getTracks()
        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(0, track)
        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeLast()
        }
        val tracksInJson = createJsonFromTracks(tracks)
        sharedPrefs.edit { putString(TRACKS_HISTORY_KEY, tracksInJson) }
    }

    fun clear() {
        sharedPrefs.edit { remove(TRACKS_HISTORY_KEY) }
    }

    fun getTracks(): MutableList<Track> {
        return createTracksFromJson(sharedPrefs.getString(TRACKS_HISTORY_KEY, null))
    }

    private fun createTracksFromJson(json: String?): MutableList<Track> {
        return if (json == null) {
            mutableListOf()
        } else {
            gson.fromJson(json, typeToken)
        }
    }

    private fun createJsonFromTracks(tracks: MutableList<Track>): String {
        return gson.toJson(tracks)
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 10
        const val TRACKS_HISTORY_KEY = "tracks_history_key"
    }
}