package com.example.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.model.Track

class TrackListAdapter : RecyclerView.Adapter<TrackListAdapter.TrackViewHolder>() {
    var tracks: List<Track> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.apply {
                tvTrackName.text = track.trackName
                tvArtistNameAndTrackTime.text = itemView.context.getString(
                    R.string.artist_name_and_track_time,
                    track.artistName,
                    track.trackTime
                )
                Glide.with(itemView)
                    .load(track.artworkUrl100)
                    .transform(RoundedCorners(ARTWORK_CORNER_RADIUS))
                    .placeholder(R.drawable.ic_track_artwork_placeholder)
                    .into(ivArtwork)
            }
        }

        private companion object {
            const val ARTWORK_CORNER_RADIUS = 10
        }
    }
}