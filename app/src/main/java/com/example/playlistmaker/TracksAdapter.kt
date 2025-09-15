package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    ) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)

        fun bind(track: Track, timeFormat: SimpleDateFormat) {
            tvTitle.text = track.trackName
            val mmss = timeFormat.format(track.trackTimeMillis)
            tvSubtitle.text = "${track.artistName} • $mmss"

            val radius = itemView.resources.getDimensionPixelSize(R.dimen.cover_radius)
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_square)
                .error(R.drawable.placeholder_square)
                .transform(CenterCrop(), RoundedCorners(radius))
                .into(ivCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(parent)
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) =
        holder.bind(tracks[position], timeFormat)
    override fun getItemCount() = tracks.size
}
