package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_audio_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val root = view.findViewById<View>(R.id.root_player)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.content_top_margin)
            v.updatePadding(top = status.top + extraTop, bottom = nav.bottom)
            insets
        }

        track = requireArguments().getParcelable(ARG_TRACK)
        if (track == null) {
            findNavController().navigateUp()
            return
        }

        bindViews(view)
        bindTrackInfo(track!!)

        viewModel.prepare(track!!.previewUrl)
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            trackTimeTextView.text = state.progress
            btnPlay.isEnabled = state.isPlayButtonEnabled
            if (state.isPlaying) setPauseIcon() else setPlayIcon()
        }

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun bindViews(view: View) {
        btnPlay = view.findViewById(R.id.btnPlay)
        backButton = view.findViewById(R.id.backButton)
        coverImageView = view.findViewById(R.id.coverImageView)
        trackTitleTextView = view.findViewById(R.id.trackTitleTextView)
        artistNameTextView = view.findViewById(R.id.artistNameTextView)
        albumTextView = view.findViewById(R.id.albumTextView)
        releaseDateTextView = view.findViewById(R.id.releaseDateTextView)
        genreTextView = view.findViewById(R.id.genreTextView)
        countryTextView = view.findViewById(R.id.countryTextView)
        trackTimeTextView = view.findViewById(R.id.trackTimeTextView)
        valueDuration = view.findViewById(R.id.valueDuration)
    }

    private fun bindTrackInfo(t: Track) {
        trackTimeTextView.text = "00:00"
        trackTitleTextView.text = t.trackName
        artistNameTextView.text = t.artistName

        if (t.collectionName.isNullOrBlank()) {
            albumTextView.visibility = View.GONE
        } else {
            albumTextView.visibility = View.VISIBLE
            albumTextView.text = t.collectionName
        }

        if (t.releaseDate.isNullOrBlank()) {
            releaseDateTextView.visibility = View.GONE
        } else {
            releaseDateTextView.visibility = View.VISIBLE
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
        const val ARG_TRACK = "ARG_TRACK"

        fun createArgs(track: Track): Bundle =
            Bundle().apply { putParcelable(ARG_TRACK, track) }
    }
}