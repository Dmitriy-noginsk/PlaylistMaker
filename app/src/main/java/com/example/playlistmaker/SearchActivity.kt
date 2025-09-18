package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private val vm: SearchViewModel by viewModels()

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
    private lateinit var searchHistory: SearchHistory

    private var searchQuery: String = ""

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

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

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
            addToHistory(track)
        }
        rvTracks.adapter = adapter

        rvHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(emptyList()) { track ->
            addToHistory(track)
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
            toggleHistory(etSearch.hasFocus(), searchQuery)
        }

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            toggleHistory(hasFocus, etSearch.text?.toString().orEmpty())
        }

        btnClear.setOnClickListener {
            etSearch.text.clear()
            hideKeyboard()
            etSearch.clearFocus()
            render(SearchState.Idle)
            toggleHistory(false, "")
        }

        btnClearHistory.setOnClickListener {
            searchHistory.clear()
            updateHistoryUi()
            toggleHistory(etSearch.hasFocus(), etSearch.text?.toString().orEmpty())
        }

        btnRetry.setOnClickListener {
            hideKeyboard()
            vm.retry()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { render(it) }
            }
        }

        updateHistoryUi()
        toggleHistory(etSearch.hasFocus(), etSearch.text?.toString().orEmpty())
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Idle -> {
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
            }
            is SearchState.Loading -> {
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
            }
            is SearchState.Empty -> {
                rvTracks.isGone = true
                placeholderError.isGone = true
                placeholderEmpty.isVisible = true
            }
            is SearchState.Error -> {
                rvTracks.isGone = true
                placeholderEmpty.isGone = true
                placeholderError.isVisible = true
            }
            is SearchState.Content -> {
                placeholderEmpty.isGone = true
                placeholderError.isGone = true
                rvTracks.isVisible = true

                adapter.setData(state.items)

                historyContainer.isGone = true
            }
        }
    }


    private fun addToHistory(track: Track) {
        searchHistory.add(track)
        updateHistoryUi()
    }

    private fun updateHistoryUi() {
        historyAdapter.setData(searchHistory.get())
    }

    private fun toggleHistory(hasFocus: Boolean, text: String) {
        val shouldShow = hasFocus && text.isEmpty() && searchHistory.isNotEmpty()
        historyContainer.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    // --- сервисные

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
        private const val PREFS_NAME = "playlist_prefs"
        private const val KEY_SEARCH_QUERY = "SEARCH_QUERY"
    }
}
