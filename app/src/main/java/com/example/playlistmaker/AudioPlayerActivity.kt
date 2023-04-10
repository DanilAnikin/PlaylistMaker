package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.model.Track
import java.util.*

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private val trackPlayer = MediaPlayer()
    private var playerState: PlayerState = PlayerState.DEFAULT
    private val simpleDateFormat = SimpleDateFormat(SearchActivity.TRACK_TIME_FORMAT_PATTERN, Locale.getDefault())
    private val handler = Handler(Looper.getMainLooper())

    private val updatePlaybackRunnable = object : Runnable {
        override fun run() {
            binding.tvTime.text = simpleDateFormat.format(trackPlayer.currentPosition)
            handler.postDelayed(this, PLAYBACK_TIME_UPDATE_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarInclude.toolbar.apply {
            title = ""
            setSupportActionBar(this)
            setNavigationOnClickListener { finish() }
        }

        val track = intent.extras!!.getParcelable<Track>(SearchActivity.TRACK_KEY)!!
        setTrackDetailsToViews(track)

        preparePlayer(track.previewUrl!!)

        binding.btnPlaybackControl.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updatePlaybackRunnable)
        trackPlayer.release()
    }

    private fun setTrackDetailsToViews(track: Track) {
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
            if (track.releaseDate != null) {
                tvReleaseDateValue.text = track.releaseDate.take(4)
            } else {
                tvReleaseDateLabel.visibility = View.GONE
                tvReleaseDateValue.visibility = View.GONE
            }
            tvGenreValue.text = track.primaryGenreName
            tvCountryValue.text = track.country
            tvTime.text = PLAYBACK_TIME_START
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED -> playTrack()
            PlayerState.PLAYING -> pauseTrack()
            PlayerState.DEFAULT -> Unit
        }
    }

    private fun preparePlayer(previewUrl: String) {
        trackPlayer.setDataSource(previewUrl)
        trackPlayer.prepareAsync()

        trackPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }

        trackPlayer.setOnCompletionListener {
            handler.removeCallbacks(updatePlaybackRunnable)
            binding.tvTime.text = PLAYBACK_TIME_START
            trackPlayer.seekTo(PLAYER_START_POSITION)
            binding.btnPlaybackControl.setImageResource(R.drawable.ic_play_arrow)
            playerState = PlayerState.PREPARED
        }
    }

    private fun playTrack() {
        startPlaybackUpdate()
        trackPlayer.start()
        binding.btnPlaybackControl.setImageResource(R.drawable.ic_pause)
        playerState = PlayerState.PLAYING
    }

    private fun pauseTrack() {
        handler.removeCallbacks(updatePlaybackRunnable)
        trackPlayer.pause()
        binding.btnPlaybackControl.setImageResource(R.drawable.ic_play_arrow)
        playerState = PlayerState.PAUSED
    }

    private fun startPlaybackUpdate() {
        handler.post(updatePlaybackRunnable)
    }

    companion object {
        const val PLAYBACK_TIME_UPDATE_DELAY = 500L
        const val PLAYBACK_TIME_START = "00:00"
        const val PLAYER_START_POSITION = 0
    }
}