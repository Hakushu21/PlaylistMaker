package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.presentation.adapters.HistoryAdapter
import com.example.playlistmaker.presentation.adapters.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var startSearchLayout: View
    private lateinit var nothingFoundLayout: View
    private lateinit var nothingFoundText: TextView
    private lateinit var nothingFoundImage: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var errorLayout: View
    private lateinit var errorMessage: TextView
    private lateinit var errorImage: ImageView
    private lateinit var retryButton: MaterialButton

    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: View

    private var searchQuery: String = ""
    private var lastFailedQuery: String? = null

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L
    private var currentTracks: List<Track> = emptyList()

    private val searchInteractor = Creator.provideSearchInteractor()

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
        private const val SEARCH_RESULTS_KEY = "SEARCH_RESULTS"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        setupWindowInsets()
        initViews()
        setupClickListeners()
        setupSearchField()
        setupRecyclerViews()

        updateHistoryVisibility()

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            val savedTracks = savedInstanceState.getSerializable(SEARCH_RESULTS_KEY) as? List<Track>

            if (!savedQuery.isNullOrEmpty()) {
                searchEditText.setText(savedQuery)
                searchQuery = savedQuery
                clearButton.visibility = View.VISIBLE

                if (!savedTracks.isNullOrEmpty()) {
                    currentTracks = savedTracks
                    showTracks(currentTracks)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
        if (currentTracks.isNotEmpty()) {
            outState.putSerializable(SEARCH_RESULTS_KEY, ArrayList(currentTracks))
        }
    }

    override fun onResume() {
        super.onResume()
        if (nothingFoundLayout.visibility == View.VISIBLE) {
            updateNothingFoundView()
        }
        if (errorLayout.visibility == View.VISIBLE) {
            updateErrorView()
        }
        updateHistoryVisibility()

        if (currentTracks.isNotEmpty() && tracksRecyclerView.visibility != View.VISIBLE) {
            showTracks(currentTracks)
        }
    }

    private fun enableEdgeToEdge() {
        window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        startSearchLayout = findViewById(R.id.start_search_layout)
        nothingFoundLayout = findViewById(R.id.nothing_found_layout)
        nothingFoundText = findViewById(R.id.nothing_found_text)
        nothingFoundImage = findViewById(R.id.nothing_found_image)
        tracksRecyclerView = findViewById(R.id.tracks_recycler_view)
        loadingProgressBar = findViewById(R.id.loading_progress_bar)
        errorLayout = findViewById(R.id.error_layout)
        errorMessage = findViewById(R.id.error_message)
        errorImage = findViewById(R.id.error_image)
        retryButton = findViewById(R.id.retry_button)

        historyTitle = findViewById(R.id.history_title)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        historyLayout = findViewById(R.id.history_layout)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            clearSearch()
        }

        retryButton.setOnClickListener {
            lastFailedQuery?.let { query ->
                performSearch(query)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                clearButton.visibility = if (searchQuery.isNotEmpty()) View.VISIBLE else View.GONE

                searchRunnable?.let { handler.removeCallbacks(it) }

                if (searchQuery.isNotEmpty()) {
                    searchRunnable = Runnable {
                        performSearch(searchQuery)
                    }
                    handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                } else {
                    updateHistoryVisibility()
                }
            }
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                showKeyboard()
            }
        }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                searchInteractor.addTrackToHistory(track)
                updateHistoryVisibility()

                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }

        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = searchAdapter

        historyAdapter = HistoryAdapter(
            emptyList(),
            onTrackClick = { track ->
                if (clickDebounce()) {
                    searchInteractor.addTrackToHistory(track)
                    updateHistoryVisibility()

                    val intent = Intent(this, PlayerActivity::class.java)
                    intent.putExtra("track", track)
                    startActivity(intent)
                }
            },
            onClearHistoryClick = {
                searchInteractor.clearSearchHistory()
                historyAdapter.clearHistory()
                updateHistoryVisibility()
            }
        )

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, clickDebounceDelay)
        }
        return current
    }

    private fun updateHistoryVisibility() {
        val hasFocus = searchEditText.hasFocus()
        val isEmpty = searchEditText.text.isNullOrEmpty()
        val history = searchInteractor.getSearchHistory()

        startSearchLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        loadingProgressBar.visibility = View.GONE

        if (isEmpty) {
            if (hasFocus && history.isNotEmpty()) {
                historyLayout.visibility = View.VISIBLE
                historyAdapter.updateTracks(history)
            } else {
                startSearchLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        hideKeyboard()
        showLoading()
        lastFailedQuery = query

        lifecycleScope.launch {
            val result = searchInteractor.searchTracks(query)
            result.onSuccess { tracks ->
                if (tracks.isNotEmpty()) {
                    showTracks(tracks)
                } else {
                    showEmptyResults()
                }
            }.onFailure {
                showError()
            }
        }
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.GONE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        loadingProgressBar.visibility = View.GONE
        tracksRecyclerView.visibility = View.VISIBLE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE

        currentTracks = tracks
        searchAdapter.updateTracks(tracks)
    }

    private fun showEmptyResults() {
        loadingProgressBar.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        currentTracks = emptyList()

        nothingFoundText.text = getString(R.string.nothing_found)
        updateNothingFoundView()
    }

    private fun showError() {
        loadingProgressBar.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        currentTracks = emptyList()

        updateErrorView()
    }

    private fun updateNothingFoundView() {
        val isDarkTheme = isDarkThemeEnabled()

        if (isDarkTheme) {
            nothingFoundImage.setImageResource(R.drawable.ic_nothing_found_dark_120)
        } else {
            nothingFoundImage.setImageResource(R.drawable.ic_nothing_found_light_120)
        }

        val textColor = ContextCompat.getColor(this, R.color.nothing_found_text)
        nothingFoundText.setTextColor(textColor)
    }

    private fun updateErrorView() {
        val isDarkTheme = isDarkThemeEnabled()

        if (isDarkTheme) {
            errorImage.setImageResource(R.drawable.ic_no_internet_dark_120)
        } else {
            errorImage.setImageResource(R.drawable.ic_no_internet_light_120)
        }

        val textColor = ContextCompat.getColor(this, R.color.error_text)
        errorMessage.setTextColor(textColor)
    }

    private fun isDarkThemeEnabled(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun showKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun clearSearch() {
        searchEditText.setText("")
        hideKeyboard()
        searchEditText.clearFocus()
        updateHistoryVisibility()
        searchAdapter.updateTracks(emptyList())
        currentTracks = emptyList()

        searchRunnable?.let { handler.removeCallbacks(it) }
    }
}