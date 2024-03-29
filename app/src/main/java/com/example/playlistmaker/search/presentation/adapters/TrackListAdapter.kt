package com.example.playlistmaker.search.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.models.Track

class TrackListAdapter : RecyclerView.Adapter<TrackListAdapter.TrackViewHolder>() {

    private val tracks: MutableList<Track> = mutableListOf()

    fun setData(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun clearData() {
        tracks.clear()
        notifyDataSetChanged()
    }

    var onTrackClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { onTrackClickListener?.invoke(track) }
    }

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.apply {
                tvTrackName.text = track.trackName
                tvArtistNameAndTrackTime.text = itemView.context.getString(
                    R.string.artist_name_and_track_time,
                    track.artistName,
                    track.trackTimeMillis
                )
                Glide.with(itemView)
                    .load(track.artworkUrl100)
                    .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.list_item_cover_corner_radius)))
                    .placeholder(R.drawable.ic_track_artwork_placeholder)
                    .into(ivArtwork)
            }
        }
    }
}