package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksAdapter(
    private var tracks: List<Track>,
    private val onItemClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    private var lastClickTs = 0L
    private val clickIntervalMs = 2000L

    inner class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    ) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvArtist: TextView = itemView.findViewById(R.id.tv_artist)
        private val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)

        private var boundTrack: Track? = null

        init {
            itemView.setOnClickListener {
                val now = System.currentTimeMillis()
                if (now - lastClickTs >= clickIntervalMs) {
                    lastClickTs = now
                    boundTrack?.let { t -> onItemClick?.invoke(t) }
                }
            }
        }

        fun bind(track: Track) {
            boundTrack = track

            tvTitle.text = track.trackName
            tvArtist.text = track.artistName

            val radius = itemView.resources.getDimensionPixelSize(R.dimen.cover_radius)
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_square)
                .error(R.drawable.placeholder_square)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(ivCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun setData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}
