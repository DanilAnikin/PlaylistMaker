package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.adapter.TrackListAdapter
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.model.Track

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

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
    }

    private fun setTrackDetailsToViews (track: Track) {
        Glide.with(this@AudioPlayerActivity)
            .load(track.getCoverArtworkUrl())
            .transform(RoundedCorners(TrackListAdapter.TrackViewHolder.ARTWORK_CORNER_RADIUS))
            .placeholder(R.drawable.ic_audio_player_cover_placeholder)
            .into(binding.ivCover)

        binding.apply {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationValue.text = track.trackTimeMillis
            tvAlbumValue.text = track.collectionName
            tvReleaseDateValue.text = track.releaseDate.take(4)
            tvGenreValue.text = track.primaryGenreName
            tvCountryValue.text = track.country
            tvTime.text = track.trackTimeMillis
        }
    }
}