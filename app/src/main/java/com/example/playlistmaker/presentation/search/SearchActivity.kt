package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.AudioPlayerActivity
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private val vm: SearchViewModel by viewModel()

    private lateinit var etSearch: EditText
    private lateinit var btnClear: ImageView
    private lateinit var rvTracks: RecyclerView
    private lateinit var adapter: TracksAdapter
    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View
    private lateinit var btnRetry: View

    private lateinit var historyContainer: View
    private lateinit var rvHistory: RecyclerView
    private lateinit var btnClearHistory: View
    private lateinit var historyAdapter: TracksAdapter

    private val progress by lazy { findViewById<View>(R.id.progress) }

    private var searchQuery: String = ""
    private var currentState: SearchState = SearchState.Idle

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val root = findViewById<View>(R.id.root_search)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.content_top_margin)
            v.updatePadding(top = status.top + extraTop, bottom = nav.bottom)
            insets
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        // --- findViewById
        etSearch = findViewById(R.id.et_search)
        btnClear = findViewById(R.id.btn_clear)

        rvTracks = findViewById(R.id.rv_tracks)
        placeholderEmpty = findViewById(R.id.placeholder_empty)
        placeholderError = findViewById(R.id.placeholder_error)
        btnRetry = findViewById(R.id.btn_retry)

        historyContainer = findViewById(R.id.historyContainer)
        rvHistory = findViewById(R.id.rv_history)
        btnClearHistory = findViewById(R.id.btn_clear_history)

        rvTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(emptyList()) { track ->
            onTrackClick(track)
        }
        rvTracks.adapter = adapter

        rvHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(emptyList()) { track ->
            onTrackClick(track)
        }
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.screenState.collect { screen ->
                    render(screen.searchState)
                    historyAdapter.setData(screen.history)
                    updateHistoryVisibility(screen.history.size)
                }
            }
        }
    }

    private fun onTrackClick(track: Track) {
        vm.onTrackClicked(track)

        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.EXTRA_TRACK, track)
        }
        startActivity(intent)
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
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY, "")
        etSearch.setText(searchQuery)
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }
}
