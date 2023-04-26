package com.example.playlistmaker.search.presentation.ui

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
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.search.presentation.adapters.TrackListAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.audioplayer.presentation.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.creator.Creator
import com.example.playlistmaker.search.presentation.SearchScreenView
import com.example.playlistmaker.search.presentation.presenter.SearchPresenter
import com.example.playlistmaker.setupToolbar
import java.util.*

class SearchActivity : AppCompatActivity(), SearchScreenView {

    private lateinit var binding: ActivitySearchBinding
    private val trackListAdapter: TrackListAdapter = TrackListAdapter()
    private val tracksHistoryAdapter: TrackListAdapter = TrackListAdapter()

    private lateinit var presenter: SearchPresenter

    private val simpleDateFormat = SimpleDateFormat(TRACK_TIME_FORMAT_PATTERN, Locale.getDefault())

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { presenter.findTracks(binding.etSearch.text.toString()) }
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbarInclude.toolbar, getString(R.string.search_screen_toolbar_title)) {
            presenter.backButtonClicked()
        }

        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SEARCH_TEXT_KEY).toString()
            binding.etSearch.setText(savedText)
        }

        val sharedPrefs = getSharedPreferences(App.PLAYLIST_MAKER_SHARED_PREFS, MODE_PRIVATE)
        presenter = Creator.providePresenter(this@SearchActivity, sharedPrefs)

        binding.etSearch.requestFocus()
        showKeyboard()

        initTracksHistoryAdapter()
        initTrackListAdapter()

        binding.searchHistory.btnClearHistory.setOnClickListener {
            presenter.clearHistoryButtonClicked()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            presenter.searchFieldFocusChanged(hasFocus, binding.etSearch.text)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                presenter.searchFieldTextChanged(binding.etSearch.hasFocus(), text)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.ivClear.setOnClickListener {
            presenter.searchFieldClearButtonClicked()
        }

        binding.placeholder.btnPlaceholderUpdate.setOnClickListener {
            presenter.findTracks(binding.etSearch.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.etSearch.text.toString())
    }

    override fun goBack() {
        finish()
    }

    override fun setClearButtonVisibility() {
        binding.ivClear.isVisible = !binding.etSearch.text.isNullOrEmpty()
    }

    override fun showHistory(tracksHistory: List<Track>) {
        hideTrackList()
        tracksHistoryAdapter.setData(tracksHistory)
        binding.searchHistory.nsvSearchHistory.visibility = View.VISIBLE
    }

    override fun hideTrackList() {
        trackListAdapter.clearData()
    }

    override fun clearSearchField() {
        binding.etSearch.text.clear()
    }

    override fun openAudioPlayerScreen(track: Track) {
        Intent(this@SearchActivity, AudioPlayerActivity::class.java).also {
            it.putExtra(TRACK_KEY, track)
            startActivity(it)
        }
    }

    override fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun hideHistory() {
        tracksHistoryAdapter.clearData()
        binding.searchHistory.nsvSearchHistory.visibility = View.GONE
    }

    override fun onLoading() {
        hideKeyboard()
        hidePlaceholder()
        hideTrackList()
        binding.pbSearchProgress.visibility = View.VISIBLE
    }

    override fun hidePlaceholder() {
        binding.pbSearchProgress.visibility = View.GONE
        binding.placeholder.apply {
            nsvPlaceholder.visibility = View.GONE
            llPlaceHolder.visibility = View.GONE
            ivPlaceholder.visibility = View.GONE
            tvPlaceholderMessage.visibility = View.GONE
            tvPlaceholderAdditionalMessage.visibility = View.GONE
            btnPlaceholderUpdate.visibility = View.GONE
        }
    }

    override fun showNothingFoundPlaceholder() {
        binding.pbSearchProgress.visibility = View.GONE
        binding.placeholder.apply {
            nsvPlaceholder.visibility = View.VISIBLE
            llPlaceHolder.visibility = View.VISIBLE
            ivPlaceholder.setImageResource(R.drawable.ic_nothing_found_placeholder)
            ivPlaceholder.visibility = View.VISIBLE
            tvPlaceholderMessage.text = getString(R.string.text_nothing_found)
            tvPlaceholderMessage.visibility = View.VISIBLE
        }
    }

    override fun showConnectionErrorPlaceholder() {
        binding.pbSearchProgress.visibility = View.GONE
        binding.placeholder.apply {
            nsvPlaceholder.visibility = View.VISIBLE
            llPlaceHolder.visibility = View.VISIBLE
            ivPlaceholder.setImageResource(R.drawable.ic_connection_error_placeholder)
            ivPlaceholder.visibility = View.VISIBLE
            tvPlaceholderMessage.text = getString(R.string.connection_error_message)
            tvPlaceholderMessage.visibility = View.VISIBLE
            tvPlaceholderAdditionalMessage.text =
                getString(R.string.connection_error_additional_message)
            tvPlaceholderAdditionalMessage.visibility = View.VISIBLE
            btnPlaceholderUpdate.visibility = View.VISIBLE
        }
    }

    override fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.ivClear.windowToken, 0)
    }

    override fun showTrackList(tracks: List<Track>) {
        binding.pbSearchProgress.visibility = View.GONE
        tracks.forEach { track ->
            track.trackTimeMillis = simpleDateFormat.format(track.trackTimeMillis?.toLong() ?: 0)
        }
        trackListAdapter.setData(tracks)
        binding.rvTrackList.scrollToPosition(0)
    }

    override fun showKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun initTrackListAdapter() {
        trackListAdapter.onTrackClickListener = { track ->
            if (trackClickDebounce()) {
                presenter.trackClicked(track)
            }
        }
        binding.rvTrackList.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = trackListAdapter
        }
    }

    private fun initTracksHistoryAdapter() {
        tracksHistoryAdapter.onTrackClickListener = { track ->
            if (trackClickDebounce()) {
                presenter.historyTrackClicked(track)
            }
        }
        binding.searchHistory.rvTracksHistory.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = tracksHistoryAdapter
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

    companion object {
        const val SEARCH_TEXT_KEY = "search_text"
        const val TRACK_TIME_FORMAT_PATTERN = "mm:ss"
        const val TRACK_KEY = "track_key"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val TRACK_CLICK_DEBOUNCE_DELAY = 1000L
    }
}