package com.example.playlistmaker.retrofit

import com.example.playlistmaker.model.Track

class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)