package com.example.playlistmaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private var lastFailedQuery: String = ""
    private var currentJob: Job? = null

    fun search(query: String) {
        if (query.isBlank()) {
            _state.value = SearchState.Idle
            return
        }
        currentJob?.cancel()
        _state.value = SearchState.Loading
        currentJob = viewModelScope.launch {
            try {
                val resp = RetrofitClient.api.search(query.trim())
                if (resp.isSuccessful) {
                    val list = resp.body()?.results.orEmpty().map { it.toDomain() }
                    _state.value = if (list.isEmpty()) SearchState.Empty else SearchState.Content(list)
                } else {
                    lastFailedQuery = query
                    _state.value = SearchState.Error(lastFailedQuery)
                }
            } catch (e: Exception) {
                lastFailedQuery = query
                _state.value = SearchState.Error(lastFailedQuery)
            }
        }
    }

    fun retry() {
        val q = (state.value as? SearchState.Error)?.lastQuery ?: lastFailedQuery
        if (q.isNotBlank()) search(q)
    }
}
