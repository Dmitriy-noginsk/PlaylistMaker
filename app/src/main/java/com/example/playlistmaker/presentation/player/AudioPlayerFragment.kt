package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()

    private var track: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = requireArguments().getParcelable(ARG_TRACK)
        if (track == null) {
            findNavController().navigateUp()
            return
        }

        bindTrackInfo(track!!)

        viewModel.prepare(track!!.previewUrl)

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.trackTimeTextView.text = state.progress
            binding.btnPlay.isEnabled = state.isPlayButtonEnabled
            if (state.isPlaying) setPauseIcon() else setPlayIcon()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun bindTrackInfo(t: Track) = with(binding) {
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

        Glide.with(this@AudioPlayerFragment)
            .load(t.getCoverArtwork())
            .placeholder(R.drawable.placeholder_square)
            .error(R.drawable.placeholder_square)
            .fallback(R.drawable.placeholder_square)
            .into(coverImageView)

        setPlayIcon()
    }

    private fun setPlayIcon() {
        binding.btnPlay.setImageResource(R.drawable.ic_round_play)
    }

    private fun setPauseIcon() {
        binding.btnPlay.setImageResource(R.drawable.ic_round_pause)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStopView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_TRACK = "ARG_TRACK"

        fun createArgs(track: Track): Bundle =
            Bundle().apply { putParcelable(ARG_TRACK, track) }
    }
}