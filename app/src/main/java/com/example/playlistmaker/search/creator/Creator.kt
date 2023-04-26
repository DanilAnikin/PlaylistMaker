package com.example.playlistmaker.search.creator

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.ITunesSearchApi
import com.example.playlistmaker.search.data.impl.SearchHistoryStorageImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.search.presentation.SearchScreenView
import com.example.playlistmaker.search.presentation.presenter.SearchPresenter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchApi = retrofit.create(ITunesSearchApi::class.java)

    private fun getInteractor(sharedPrefs: SharedPreferences): SearchInteractor {
        return SearchInteractorImpl(
            searchHistoryStorage = SearchHistoryStorageImpl(sharedPrefs),
            repository = TracksRepositoryImpl(iTunesSearchApi)
        )
    }

    fun providePresenter(view: SearchScreenView, sharedPrefs: SharedPreferences): SearchPresenter {
        return SearchPresenter(
            view = view,
            interactor = getInteractor(sharedPrefs)
        )
    }
}