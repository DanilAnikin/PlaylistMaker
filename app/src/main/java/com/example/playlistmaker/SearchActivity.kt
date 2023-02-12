package com.example.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.adapter.TrackListAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.retrofit.ITunesSearchApi
import com.example.playlistmaker.retrofit.TracksResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchFieldText: String = ""
    private val trackListAdapter: TrackListAdapter = TrackListAdapter()
    private val trackList: MutableList<Track> = mutableListOf()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchApi = retrofit.create(ITunesSearchApi::class.java)
    private val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            searchFieldText = savedInstanceState.getString(SEARCH_TEXT_KEY).toString()
            binding.etSearch.setText(searchFieldText)
            findTracks()
        }

        binding.toolbarInclude.toolbar.apply {
            title = getString(R.string.search_screen_toolbar_title)
            setSupportActionBar(this)
            setNavigationOnClickListener { finish() }
        }

        trackListAdapter.tracks = trackList
        binding.rvTrackList.apply {
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = trackListAdapter
        }

        binding.etSearch.requestFocus()
        showKeyboard()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.ivClear.isVisible = !binding.etSearch.text.isNullOrEmpty()
                searchFieldText = binding.etSearch.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            hideKeyboard()
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            hidePlaceholder()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.btnPlaceholderUpdate.setOnClickListener {
            findTracks()
        }
    }

    private fun findTracks() {
        if (searchFieldText.isEmpty()) {
            return
        }
        iTunesSearchApi.getTracks(searchFieldText).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() != 200) {
                    showPlaceholder(
                        R.drawable.ic_connection_error_placeholder,
                        getString(R.string.connection_error_message),
                        getString(R.string.connection_error_additional_message)
                    )
                    return
                }
                hidePlaceholder()
                if (response.body()?.results?.isNotEmpty() == true) {
                    trackList.clear()
                    trackList.addAll(response.body()!!.results)
                    trackList.forEach { track ->
                        track.trackTimeMillis =
                            simpleDateFormat.format(track.trackTimeMillis.toLong())
                    }
                    trackListAdapter.notifyDataSetChanged()
                    binding.rvTrackList.scrollToPosition(0)
                } else {
                    showPlaceholder(
                        R.drawable.ic_nothing_found_placeholder,
                        getString(R.string.text_nothing_found)
                    )
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showPlaceholder(
                    R.drawable.ic_connection_error_placeholder,
                    getString(R.string.connection_error_message),
                    getString(R.string.connection_error_additional_message)
                )
            }
        })
    }

    private fun hidePlaceholder() {
        binding.apply {
            llPlaceHolder.visibility = View.GONE
            ivPlaceholder.visibility = View.GONE
            tvPlaceholderMessage.visibility = View.GONE
            tvPlaceholderAdditionalMessage.visibility = View.GONE
            btnPlaceholderUpdate.visibility = View.GONE
        }
    }

    private fun showPlaceholder(iconId: Int, message: String, additionalMessage: String = "") {
        trackList.clear()
        trackListAdapter.notifyDataSetChanged()
        binding.apply {
            llPlaceHolder.visibility = View.VISIBLE
            ivPlaceholder.setImageResource(iconId)
            ivPlaceholder.visibility = View.VISIBLE
            tvPlaceholderMessage.text = message
            tvPlaceholderMessage.visibility = View.VISIBLE
            if (additionalMessage.isNotEmpty()) {
                tvPlaceholderAdditionalMessage.text = additionalMessage
                tvPlaceholderAdditionalMessage.visibility = View.VISIBLE
                btnPlaceholderUpdate.visibility = View.VISIBLE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchFieldText)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.ivClear.windowToken, 0)
    }

    private fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    companion object {
        const val SEARCH_TEXT_KEY = "search_text"
        const val BASE_URL = "https://itunes.apple.com"
    }
}