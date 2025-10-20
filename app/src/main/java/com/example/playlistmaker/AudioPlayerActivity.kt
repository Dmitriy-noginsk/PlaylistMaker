package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity(R.layout.activity_audioplayer) {

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

    private var mediaPlayer: MediaPlayer? = null
    private val uiHandler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (playerState == PlayerState.PLAYING) {
                updateProgress()
                uiHandler.postDelayed(this, 500)
            }
        }
    }

    private enum class PlayerState { DEFAULT, PREPARED, PLAYING, PAUSED }
    private var playerState: PlayerState = PlayerState.DEFAULT

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
        initPlayer(track!!.previewUrl)

        backButton.setOnClickListener {
            stopAndRelease()
            onBackPressedDispatcher.onBackPressed()
        }

        btnPlay.setOnClickListener {
            when (playerState) {
                PlayerState.PREPARED, PlayerState.PAUSED -> startPlayback()
                PlayerState.PLAYING -> pausePlayback()
                else -> Unit
            }
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
        valueDuration.text = formatMillis(t.trackTimeMillis)
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
        trackTimeTextView.text = "00:00"

        Glide.with(this)
            .load(t.getCoverArtwork())
            .placeholder(R.drawable.placeholder_square)
            .error(R.drawable.placeholder_square)
            .fallback(R.drawable.placeholder_square)
            .into(coverImageView)

        setPlayIcon()
    }

    private fun initPlayer(previewUrl: String?) {
        if (previewUrl.isNullOrBlank()) {
            btnPlay.isEnabled = false
            return
        }
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            setOnPreparedListener {
                playerState = PlayerState.PREPARED
                setPlayIcon()
            }
            setOnCompletionListener {
                onCompleted()
            }
            setOnErrorListener { _, _, _ ->
                playerState = PlayerState.PREPARED
                stopTicker()
                trackTimeTextView.text = "00:00"
                setPlayIcon()
                true
            }
            prepareAsync()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        playerState = PlayerState.PLAYING
        setPauseIcon()
        startTicker()
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        playerState = PlayerState.PAUSED
        setPlayIcon()
        stopTicker()
    }

    private fun onCompleted() {
        playerState = PlayerState.PREPARED
        setPlayIcon()
        stopTicker()
        trackTimeTextView.text = "00:00"
        mediaPlayer?.seekTo(0)
    }

    private fun startTicker() {
        uiHandler.removeCallbacks(tickRunnable)
        uiHandler.post(tickRunnable)
    }

    private fun stopTicker() {
        uiHandler.removeCallbacks(tickRunnable)
    }

    private fun updateProgress() {
        val pos = mediaPlayer?.currentPosition ?: 0
        trackTimeTextView.text = formatMillis(pos.toLong())
    }

    private fun stopAndRelease() {
        stopTicker()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = PlayerState.DEFAULT
    }

    private fun setPlayIcon() {
        btnPlay.setImageResource(R.drawable.ic_round_play)
    }

    private fun setPauseIcon() {
        btnPlay.setImageResource(R.drawable.ic_round_pause)
    }

    private fun formatMillis(ms: Long?): String {
        if (ms == null) return "00:00"
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    override fun onStop() {
        super.onStop()
        if (playerState == PlayerState.PLAYING) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndRelease()
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }
}
