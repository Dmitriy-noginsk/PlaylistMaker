package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity(R.layout.activity_audioplayer) {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val root = findViewById<View>(R.id.root_player)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.content_top_margin)
            v.updatePadding(top = status.top + extraTop, bottom = nav.bottom)
            insets
        }

        val track: Track? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRACK)
        }

        if (track == null) {
            finish()
            return
        }

        val valueDuration: TextView = findViewById(R.id.valueDuration)
        valueDuration.text = formatMillis(track.trackTimeMillis)
        val backButton: ImageButton = findViewById(R.id.backButton)
        val coverImageView: ImageView = findViewById(R.id.coverImageView)
        val trackTitleTextView: TextView = findViewById(R.id.trackTitleTextView)
        val artistNameTextView: TextView = findViewById(R.id.artistNameTextView)
        val albumTextView: TextView = findViewById(R.id.albumTextView)
        val releaseDateTextView: TextView = findViewById(R.id.releaseDateTextView)
        val genreTextView: TextView = findViewById(R.id.genreTextView)
        val countryTextView: TextView = findViewById(R.id.countryTextView)
        val trackTimeTextView: TextView = findViewById(R.id.trackTimeTextView)

        trackTitleTextView.text = track.trackName
        artistNameTextView.text = track.artistName

        if (track.collectionName.isNullOrBlank()) {
            albumTextView.visibility = android.view.View.GONE
        } else {
            albumTextView.text = track.collectionName
        }

        if (track.releaseDate.isNullOrBlank()) {
            releaseDateTextView.visibility = android.view.View.GONE
        } else {
            releaseDateTextView.text = track.releaseDate.take(4)
        }

        genreTextView.text = track.primaryGenreName.orEmpty()
        countryTextView.text = track.country.orEmpty()
        trackTimeTextView.text = formatMillis(track.trackTimeMillis)

        val bigCoverUrl = track.getCoverArtwork() // artworkUrl100 -> 512x512
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_square)
            .error(R.drawable.placeholder_square)
            .fallback(R.drawable.placeholder_square)
            .into(coverImageView)

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun formatMillis(ms: Long?): String {
        if (ms == null) return ""
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
