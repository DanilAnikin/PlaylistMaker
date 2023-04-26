package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.ITunesSearchApi
import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.domain.api.TracksLoadResultListener
import com.example.playlistmaker.search.domain.api.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(private val api: ITunesSearchApi) : TracksRepository {

    private var call: Call<TracksResponse> = api.getTracks("")

    override var tracksLoadResultListener: TracksLoadResultListener? = null

    override fun loadTracks(query: String) {
        call = api.getTracks(query)
        call.enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() != 200 && response.code() != 404) return

                val tracks = response.body()!!.results.filter { track ->
                    track.previewUrl != null
                }

                tracksLoadResultListener?.onSuccess(tracks)
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                if (!call.isCanceled) {
                    tracksLoadResultListener?.onError()
                }
            }
        })
    }

    override fun cancelLoading() {
        call.cancel()
    }
}