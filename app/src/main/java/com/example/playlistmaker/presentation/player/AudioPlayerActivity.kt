package com.example.playlistmaker.presentation.player

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity(R.layout.activity_audioplayer) {

    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var btnPlay: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var trackTitleTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var albumTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var trackTimeTextView: TextView
    private lateinit var valueDuration: TextView

    private var track: Track? = null

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

        track = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRACK)
        }
        if (track == null) {
            finish(); return
        }

        bindViews()
        bindTrackInfo(track!!)

        viewModel.prepare(track!!.previewUrl)

        viewModel.uiState.observe(this) { state ->
            trackTimeTextView.text = state.progress
            btnPlay.isEnabled = state.isPlayButtonEnabled
            if (state.isPlaying) setPauseIcon() else setPlayIcon()
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun bindViews() {
        btnPlay = findViewById(R.id.btnPlay)
        backButton = findViewById(R.id.backButton)
        coverImageView = findViewById(R.id.coverImageView)
        trackTitleTextView = findViewById(R.id.trackTitleTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        albumTextView = findViewById(R.id.albumTextView)
        releaseDateTextView = findViewById(R.id.releaseDateTextView)
        genreTextView = findViewById(R.id.genreTextView)
        countryTextView = findViewById(R.id.countryTextView)
        trackTimeTextView = findViewById(R.id.trackTimeTextView)
        valueDuration = findViewById(R.id.valueDuration)
    }

    private fun bindTrackInfo(t: Track) {
        trackTimeTextView.text = "00:00"
        trackTitleTextView.text = t.trackName
        artistNameTextView.text = t.artistName

        if (t.collectionName.isNullOrBlank()) {
            albumTextView.visibility = View.GONE
        } else {
            albumTextView.text = t.collectionName
        }

        if (t.releaseDate.isNullOrBlank()) {
            releaseDateTextView.visibility = View.GONE
        } else {
            releaseDateTextView.text = t.releaseDate.take(4)
        }

        genreTextView.text = t.primaryGenreName.orEmpty()
        countryTextView.text = t.country.orEmpty()

        Glide.with(this)
            .load(t.getCoverArtwork())
            .placeholder(R.drawable.placeholder_square)
            .error(R.drawable.placeholder_square)
            .fallback(R.drawable.placeholder_square)
            .into(coverImageView)

        setPlayIcon()
    }

    private fun setPlayIcon() {
        btnPlay.setImageResource(R.drawable.ic_round_play)
    }

    private fun setPauseIcon() {
        btnPlay.setImageResource(R.drawable.ic_round_pause)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStopView()
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
