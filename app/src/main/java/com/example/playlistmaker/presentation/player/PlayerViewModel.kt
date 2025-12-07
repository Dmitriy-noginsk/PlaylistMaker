package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private enum class PlayerState { DEFAULT, PREPARED, PLAYING, PAUSED }

    data class UiState(
        val progress: String = "00:00",
        val isPlayButtonEnabled: Boolean = false,
        val isPlaying: Boolean = false
    )

    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    private var playerState: PlayerState = PlayerState.DEFAULT

    private val uiHandler = Handler(Looper.getMainLooper())
    private val tickRunnable = object : Runnable {
        override fun run() {
            if (playerState == PlayerState.PLAYING) {
                updateProgress()
                uiHandler.postDelayed(this, TICK_DELAY_MS)
            }
        }
    }

    fun prepare(previewUrl: String?) {
        if (previewUrl.isNullOrBlank()) {
            _uiState.value = _uiState.value?.copy(
                isPlayButtonEnabled = false,
                progress = "00:00",
                isPlaying = false
            )
            return
        }

        mediaPlayer.reset()
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
            _uiState.value = _uiState.value?.copy(
                isPlayButtonEnabled = true,
                isPlaying = false,
                progress = "00:00"
            )
        }
        mediaPlayer.setOnCompletionListener {
            onCompleted()
        }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            playerState = PlayerState.PREPARED
            stopTicker()
            resetProgress()
            true
        }
        mediaPlayer.prepareAsync()
    }

    fun onPlayClicked() {
        when (playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayback()
            PlayerState.PLAYING -> pausePlayback()
            else -> Unit
        }
    }

    fun onStopView() {
        if (playerState == PlayerState.PLAYING) {
            pausePlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        playerState = PlayerState.PLAYING
        _uiState.value = _uiState.value?.copy(
            isPlaying = true
        )
        startTicker()
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        playerState = PlayerState.PAUSED
        _uiState.value = _uiState.value?.copy(
            isPlaying = false
        )
        stopTicker()
    }

    private fun onCompleted() {
        playerState = PlayerState.PREPARED
        stopTicker()
        mediaPlayer?.seekTo(0)
        resetProgress()
        _uiState.value = _uiState.value?.copy(
            isPlaying = false
        )
    }

    private fun startTicker() {
        uiHandler.removeCallbacks(tickRunnable)
        uiHandler.post(tickRunnable)
    }

    private fun stopTicker() {
        uiHandler.removeCallbacks(tickRunnable)
    }

    private fun updateProgress() {
        val ms = (mediaPlayer?.currentPosition ?: 0).toLong()
        _uiState.value = _uiState.value?.copy(
            progress = timeFormat.format(ms)
        )
    }

    private fun resetProgress() {
        _uiState.value = _uiState.value?.copy(
            progress = "00:00"
        )
    }

    private fun stopAndRelease() {
        stopTicker()

        try {
            mediaPlayer.stop()
        } catch (_: IllegalStateException) {
        }

        mediaPlayer.reset()
        playerState = PlayerState.DEFAULT
    }

    override fun onCleared() {
        super.onCleared()
        stopAndRelease()
    }

    companion object {
        private const val TICK_DELAY_MS = 500L
    }
}
