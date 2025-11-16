package com.example.playlistmaker.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.HistoryInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor,
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        SearchScreenState(
            query = "",
            searchState = SearchState.Idle,
            history = historyInteractor.getHistory()
        )
    )
    val screenState: StateFlow<SearchScreenState> = _screenState

    private var lastFailedQuery: String = ""
    private var currentJob: Job? = null
    private val queryFlow = MutableStateFlow("")

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is CancellationException) return@CoroutineExceptionHandler
        _screenState.value = _screenState.value.copy(
            searchState = SearchState.Error(lastFailedQuery)
        )
    }

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(2000)
                .distinctUntilChanged()
                .collect { q -> performSearch(q) }
        }
    }

    fun onQueryChanged(newText: String) {
        _screenState.value = _screenState.value.copy(query = newText)
        queryFlow.value = newText
    }

    fun search(query: String) = performSearch(query)

    fun retry() {
        val current = _screenState.value.searchState
        val q = (current as? SearchState.Error)?.lastQuery ?: lastFailedQuery
        if (q.isNotBlank()) performSearch(q)
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.addToHistory(track)
        _screenState.value = _screenState.value.copy(
            history = historyInteractor.getHistory()
        )
    }

    fun onClearHistoryClicked() {
        historyInteractor.clearHistory()
        _screenState.value = _screenState.value.copy(history = emptyList())
    }

    private fun performSearch(raw: String) {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) {
            currentJob?.cancel()
            _screenState.value = _screenState.value.copy(
                searchState = SearchState.Idle
            )
            return
        }

        lastFailedQuery = trimmed
        currentJob?.cancel()
        _screenState.value = _screenState.value.copy(
            searchState = SearchState.Loading
        )

        currentJob = viewModelScope.launch(errorHandler) {
            tracksInteractor.search(trimmed)
                .onSuccess { list ->
                    _screenState.value = _screenState.value.copy(
                        searchState = if (list.isEmpty()) SearchState.Empty
                        else SearchState.Content(list)
                    )
                }
                .onFailure {
                    _screenState.value = _screenState.value.copy(
                        searchState = SearchState.Error(lastFailedQuery)
                    )
                }
        }
    }
}
