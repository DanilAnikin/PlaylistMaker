package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track

class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)