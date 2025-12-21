package com.example.playlistmaker.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.AudioPlayerFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val vm: SearchViewModel by viewModel()

    private lateinit var etSearch: EditText
    private lateinit var btnClear: ImageView
    private lateinit var rvTracks: RecyclerView
    private lateinit var adapter: TracksAdapter
    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View
    private lateinit var btnRetry: View
    private lateinit var progress: View

    private lateinit var historyContainer: View
    private lateinit var rvHistory: RecyclerView
    private lateinit var btnClearHistory: View
    private lateinit var historyAdapter: TracksAdapter

    private var searchQuery: String = ""
    private var currentState: SearchState = SearchState.Idle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.et_search)
        btnClear = view.findViewById(R.id.btn_clear)

        rvTracks = view.findViewById(R.id.rv_tracks)
        placeholderEmpty = view.findViewById(R.id.placeholder_empty)
        placeholderError = view.findViewById(R.id.placeholder_error)
        btnRetry = view.findViewById(R.id.btn_retry)
        progress = view.findViewById(R.id.progress)

        historyContainer = view.findViewById(R.id.historyContainer)
        rvHistory = view.findViewById(R.id.rv_history)
        btnClearHistory = view.findViewById(R.id.btn_clear_history)

        rvTracks.layoutManager = LinearLayoutManager(requireContext())
        adapter = TracksAdapter(emptyList()) { track -> onTrackClick(track) }
        rvTracks.adapter = adapter

        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TracksAdapter(emptyList()) { track -> onTrackClick(track) }
        rvHistory.adapter = historyAdapter

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                vm.search(etSearch.text.toString())
                true
            } else false
        }

        etSearch.doOnTextChanged { text, _, _, _ ->
            searchQuery = text?.toString().orEmpty()
            btnClear.visibility = if (searchQuery.isEmpty()) View.GONE else View.VISIBLE
            vm.onQueryChanged(searchQuery)
            updateHistoryVisibility()
        }

        etSearch.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }

        btnClear.setOnClickListener {
            etSearch.text.clear()
            hideKeyboard()
            etSearch.clearFocus()
            vm.onQueryChanged("")
            render(SearchState.Idle)
            updateHistoryVisibility()
        }

        btnClearHistory.setOnClickListener {
            vm.onClearHistoryClicked()
        }

        btnRetry.setOnClickListener {
            hideKeyboard()
            vm.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.screenState.collect { screen ->
                    render(screen.searchState)
                    historyAdapter.setData(screen.history)
                    updateHistoryVisibility(screen.history.size)
                }
            }
        }

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY, "")
            etSearch.setText(searchQuery)
        }
    }

    private fun onTrackClick(track: Track) {
        vm.onTrackClicked(track)

        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            AudioPlayerFragment.createArgs(track)
        )
    }

    private fun render(state: SearchState) {
        currentState = state

        when (state) {
            is SearchState.Idle -> {
                progress.isGone = true
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
            }

            is SearchState.Loading -> {
                progress.isVisible = true
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
                historyContainer.isGone = true
            }

            is SearchState.Empty -> {
                progress.isGone = true
                rvTracks.isGone = true
                placeholderError.isGone = true
                placeholderEmpty.isVisible = true
                historyContainer.isGone = true
            }

            is SearchState.Error -> {
                progress.isGone = true
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isVisible = true
                historyContainer.isGone = true
            }

            is SearchState.Content -> {
                progress.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
                rvTracks.isVisible = true
                adapter.setData(state.items)
                historyContainer.isGone = true
            }
        }
    }

    private fun updateHistoryVisibility(historySize: Int = historyAdapter.itemCount) {
        val hasFocus = etSearch.hasFocus()
        val text = etSearch.text?.toString().orEmpty()

        val shouldShow =
            currentState is SearchState.Idle &&
                    hasFocus &&
                    text.isEmpty() &&
                    historySize > 0

        historyContainer.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }
}