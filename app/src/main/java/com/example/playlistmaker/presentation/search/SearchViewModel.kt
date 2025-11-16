package com.example.playlistmaker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history: StateFlow<List<Track>> = _history

    private var lastFailedQuery: String = ""
    private var currentJob: Job? = null
    private val queryFlow = MutableStateFlow("")

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is CancellationException) return@CoroutineExceptionHandler
        _state.value = SearchState.Error(lastFailedQuery)
    }

    init {
        _history.value = historyInteractor.getHistory()

        viewModelScope.launch {
            queryFlow
                .debounce(2000)
                .distinctUntilChanged()
                .collect { q -> performSearch(q) }
        }
    }

    fun onQueryChanged(newText: String) {
        queryFlow.value = newText
    }

    fun search(query: String) = performSearch(query)

    fun retry() {
        val q = (state.value as? SearchState.Error)?.lastQuery ?: lastFailedQuery
        if (q.isNotBlank()) performSearch(q)
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.addToHistory(track)
        _history.value = historyInteractor.getHistory()
    }

    fun onClearHistoryClicked() {
        historyInteractor.clearHistory()
        _history.value = emptyList()
    }

    private fun performSearch(raw: String) {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) {
            currentJob?.cancel()
            _state.value = SearchState.Idle
            return
        }

        lastFailedQuery = trimmed
        currentJob?.cancel()
        _state.value = SearchState.Loading

        currentJob = viewModelScope.launch(errorHandler) {
            tracksInteractor.search(trimmed)
                .onSuccess { list ->
                    _state.value = if (list.isEmpty()) SearchState.Empty
                    else SearchState.Content(list)
                }
                .onFailure {
                    _state.value = SearchState.Error(lastFailedQuery)
                }
        }
    }
}
