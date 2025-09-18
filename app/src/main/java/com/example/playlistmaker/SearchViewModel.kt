package com.example.playlistmaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private var lastFailedQuery: String = ""
    private var currentJob: Job? = null

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is CancellationException) return@CoroutineExceptionHandler
        _state.value = SearchState.Error(lastFailedQuery)
    }

    fun search(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _state.value = SearchState.Idle
            return
        }

        lastFailedQuery = trimmed
        currentJob?.cancel()
        _state.value = SearchState.Loading
        currentJob = viewModelScope.launch(errorHandler) {
            val resp = RetrofitClient.api.search(trimmed)

            if (resp.isSuccessful) {
                val list = resp.body()?.results.orEmpty().map { it.toDomain() }
                _state.value = if (list.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Content(list)
                }
            } else {
                _state.value = SearchState.Error(lastFailedQuery)
            }
        }
    }

    fun retry() {
        val q = (state.value as? SearchState.Error)?.lastQuery ?: lastFailedQuery
        if (q.isNotBlank()) search(q)
    }
}
