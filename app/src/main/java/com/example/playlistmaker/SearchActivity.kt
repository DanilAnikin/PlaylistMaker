package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.audioplayer.presentation.ui.AudioPlayerActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchFieldText: String = ""
    private val trackListAdapter: TrackListAdapter = TrackListAdapter()
    private var trackList: MutableList<Track> = mutableListOf()
    private val tracksHistoryAdapter: TrackListAdapter = TrackListAdapter()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchApi = retrofit.create(ITunesSearchApi::class.java)
    private val simpleDateFormat = SimpleDateFormat(TRACK_TIME_FORMAT_PATTERN, Locale.getDefault())

    private val searchHistoryStorage by lazy {
        val sharedPrefs = getSharedPreferences(App.PLAYLIST_MAKER_SHARED_PREFS, MODE_PRIVATE)
        SearchHistoryStorage(sharedPrefs)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { findTracks() }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbarInclude.toolbar, getString(R.string.search_screen_toolbar_title)) {
            finish()
        }

        if (savedInstanceState != null) {
            searchFieldText = savedInstanceState.getString(SEARCH_TEXT_KEY).toString()
            binding.etSearch.setText(searchFieldText)
            findTracks()
        }

        binding.etSearch.requestFocus()
        showKeyboard()

        tracksHistoryAdapter.setData(searchHistoryStorage.getTracks())
        tracksHistoryAdapter.onTrackClickListener = { track ->
            if (trackClickDebounce()) {
                startAudioPlayerScreen(track)
            }
        }
        binding.searchHistory.rvTracksHistory.apply {
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = tracksHistoryAdapter
        }

        showHistory()

        binding.searchHistory.btnClearHistory.setOnClickListener {
            searchHistoryStorage.clear()
            tracksHistoryAdapter.clearData()
            hideHistory()
        }

        trackListAdapter.setData(trackList)
        trackListAdapter.onTrackClickListener = { track ->
            if(trackClickDebounce()) {
                searchHistoryStorage.addTrack(track)
                startAudioPlayerScreen(track)
            }
        }
        binding.rvTrackList.apply {
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = trackListAdapter
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etSearch.text.isEmpty()) {
                showHistory()
            } else {
                hideHistory()
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.ivClear.isVisible = !binding.etSearch.text.isNullOrEmpty()
                searchFieldText = binding.etSearch.text.toString()

                if (binding.etSearch.hasFocus() && text?.isEmpty() == true) {
                    showHistory()
                } else {
                    hideHistory()
                }

                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.ivClear.setOnClickListener {
            binding.etSearch.text.clear()
            trackListAdapter.clearData()
            hidePlaceholder()
            hideKeyboard()
        }

        binding.placeholder.btnPlaceholderUpdate.setOnClickListener {
            findTracks()
        }
    }

    private fun trackClickDebounce(): Boolean {
        val current = isClickAllowed
        if (current) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, TRACK_CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showHistory() {
        tracksHistoryAdapter.setData(searchHistoryStorage.getTracks())
        if (!tracksHistoryAdapter.isDataEmpty()) {
            trackListAdapter.clearData()
            binding.rvTrackList.visibility = View.GONE
            binding.searchHistory.nsvSearchHistory.visibility = View.VISIBLE
            hidePlaceholder()
        }
    }

    private fun hideHistory() {
        binding.searchHistory.nsvSearchHistory.visibility = View.GONE
        binding.rvTrackList.visibility = View.VISIBLE
    }

    private fun findTracks() {
        if (searchFieldText.isEmpty()) return

        hideKeyboard()
        hidePlaceholder()
        binding.rvTrackList.visibility = View.GONE
        binding.pbSearchProgress.visibility = View.VISIBLE

        iTunesSearchApi.getTracks(searchFieldText).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                binding.pbSearchProgress.visibility = View.GONE
                binding.rvTrackList.visibility = View.VISIBLE

                if (response.code() != 200 && response.code() != 404) {
                    showPlaceholder(
                        R.drawable.ic_connection_error_placeholder,
                        getString(R.string.connection_error_message),
                        getString(R.string.connection_error_additional_message)
                    )
                    return
                }
                hidePlaceholder()
                if (response.body()?.results?.isNotEmpty() == true) {
                    trackList = response.body()!!.results.filter { track ->
                        track.previewUrl != null
                    }.toMutableList()

                    trackList.forEach { track ->
                        track.trackTimeMillis =
                            simpleDateFormat.format(track.trackTimeMillis?.toLong() ?: 0)
                    }
                    trackListAdapter.setData(trackList)
                    binding.rvTrackList.scrollToPosition(0)
                } else {
                    showPlaceholder(
                        R.drawable.ic_nothing_found_placeholder,
                        getString(R.string.text_nothing_found)
                    )
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                binding.pbSearchProgress.visibility = View.GONE
                showPlaceholder(
                    R.drawable.ic_connection_error_placeholder,
                    getString(R.string.connection_error_message),
                    getString(R.string.connection_error_additional_message)
                )
            }
        })
    }

    private fun hidePlaceholder() {
        binding.placeholder.apply {
            nsvPlaceholder.visibility = View.GONE
            llPlaceHolder.visibility = View.GONE
            ivPlaceholder.visibility = View.GONE
            tvPlaceholderMessage.visibility = View.GONE
            tvPlaceholderAdditionalMessage.visibility = View.GONE
            btnPlaceholderUpdate.visibility = View.GONE
        }
    }

    private fun showPlaceholder(iconId: Int, message: String, additionalMessage: String = "") {
        trackListAdapter.clearData()
        binding.placeholder.apply {
            nsvPlaceholder.visibility = View.VISIBLE
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

    private fun startAudioPlayerScreen(track: Track) {
        Intent(this@SearchActivity, AudioPlayerActivity::class.java).also {
            it.putExtra(TRACK_KEY, track)
            startActivity(it)
        }
    }

    companion object {
        const val SEARCH_TEXT_KEY = "search_text"
        const val BASE_URL = "https://itunes.apple.com"
        const val TRACK_TIME_FORMAT_PATTERN = "mm:ss"
        const val TRACK_KEY = "track_key"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L
    }
}