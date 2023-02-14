package com.example.playlistmaker.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {
    @GET("/search?entity=song")
    fun getTracks(@Query("term") trackName: String): Call<TracksResponse>
}