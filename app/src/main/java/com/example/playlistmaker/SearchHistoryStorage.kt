package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.LruCache
import androidx.core.content.edit
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(private val sharedPrefs: SharedPreferences) {
    private val gson: Gson = Gson()
    private val typeToken = object : TypeToken<LruCache<Int, Track>>() {}.type

    fun addTrack(track: Track) {
        val jsonTracks = sharedPrefs.getString(TRACKS_HISTORY_KEY, null)
        val lruTracks = createTracksFromJson(jsonTracks)

        if (lruTracks.get(track.trackId) == null) {
            lruTracks.put(track.trackId, track)
        }
        sharedPrefs.edit { putString(TRACKS_HISTORY_KEY, createJsonFromTracks(lruTracks)) }

//        tracks.removeIf { it.trackId == track.trackId }
//        tracks.add(0, track)
//        if (tracks.size > MAX_HISTORY_SIZE) {
//            tracks.removeLast()
//        }
//        val tracksInJson = createJsonFromTracks(tracks)
//        sharedPrefs.edit { putString(TRACKS_HISTORY_KEY, tracksInJson) }
    }

    fun clear() {
        sharedPrefs.edit { remove(TRACKS_HISTORY_KEY) }
    }

    fun getTracks(): List<Track> {
        val mapOfTracks = createTracksFromJson(sharedPrefs.getString(TRACKS_HISTORY_KEY, null)).snapshot()
        return mapOfTracks.values.reversed()
    }

    private fun createTracksFromJson(json: String?): LruCache<Int, Track> {
        return if (json == null) {
            LruCache(MAX_HISTORY_SIZE)
        } else {
            gson.fromJson<LruCache<Int, Track>?>(json, typeToken).also { it.resize(MAX_HISTORY_SIZE) }
        }
    }

    private fun createJsonFromTracks(tracks: LruCache<Int, Track>): String {
        return gson.toJson(tracks)
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 10
        const val TRACKS_HISTORY_KEY = "tracks_history_key"
    }
}