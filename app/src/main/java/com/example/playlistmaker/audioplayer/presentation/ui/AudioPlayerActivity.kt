package com.example.playlistmaker.audioplayer.presentation.ui

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchActivity
import com.example.playlistmaker.audioplayer.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.audioplayer.presentation.AudioPlayerView
import com.example.playlistmaker.audioplayer.presentation.presenter.AudioPlayerPresenter
import com.example.playlistmaker.setupToolbar
import java.util.*

class AudioPlayerActivity : AppCompatActivity(), AudioPlayerView {

    private val simpleDateFormat = SimpleDateFormat(SearchActivity.TRACK_TIME_FORMAT_PATTERN, Locale.getDefault())
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var presenter: AudioPlayerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding.toolbarInclude.toolbar) { presenter.onBackButtonClicked() }

        val track = intent.extras!!.getParcelable<Track>(SearchActivity.TRACK_KEY)!!
        presenter = Creator.providePresenter(this@AudioPlayerActivity, track)

        binding.btnPlaybackControl.setOnClickListener {
            presenter.playbackControl()
        }

        presenter.drawPlayer(track)
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewHidden()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroyed()
    }

    override fun setPlayIcon() {
        binding.btnPlaybackControl.setImageResource(R.drawable.ic_play_arrow)
    }

    override fun setPauseIcon() {
        binding.btnPlaybackControl.setImageResource(R.drawable.ic_pause)
    }

    override fun updatePlaybackTime(currentTime: Int) {
        binding.tvTime.text = simpleDateFormat.format(currentTime)
    }

    override fun showReleaseDate(releaseDate: String) {
        binding.tvReleaseDateValue.text = releaseDate
    }

    override fun hideReleaseDate() {
        binding.tvReleaseDateLabel.visibility = View.GONE
        binding.tvReleaseDateValue.visibility = View.GONE
    }

    override fun goBack() {
        finish()
    }

    override fun drawPlayer(track: Track) {
        Glide.with(this@AudioPlayerActivity)
            .load(track.getCoverArtworkUrl())
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.audio_player_cover_corner_radius)))
            .placeholder(R.drawable.ic_audio_player_cover_placeholder)
            .into(binding.ivCover)

        binding.apply {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationValue.text = track.trackTimeMillis
            tvAlbumValue.text = track.collectionName
            presenter.showReleaseDateIfNeeded(track.releaseDate)
            tvGenreValue.text = track.primaryGenreName
            tvCountryValue.text = track.country
            tvTime.text = TEXT_TIME_START
        }
    }

    companion object {
        const val TEXT_TIME_START = "00:00"
    }
}