package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {
    @GET("/search?entity=song")
    fun getTracks(@Query("term") query: String): Call<TracksResponse>
}