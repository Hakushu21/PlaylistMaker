package com.example.playlistmaker

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var startSearchLayout: View
    private lateinit var nothingFoundLayout: View
    private lateinit var nothingFoundText: TextView
    private lateinit var nothingFoundImage: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: View
    private lateinit var errorLayout: View
    private lateinit var errorMessage: TextView
    private lateinit var errorImage: ImageView
    private lateinit var retryButton: MaterialButton

    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: View

    private var searchQuery: String = ""
    private var currentSearchCall: Call<SearchResponse>? = null
    private lateinit var searchHistory: SearchHistory
    private var lastFailedQuery: String? = null

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(
            getSharedPreferences("search_history", MODE_PRIVATE)
        )

        initViews()
        setupClickListeners()
        setupSearchField()
        setupRecyclerViews()

        updateHistoryVisibility()
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
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchField() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchQuery = text?.toString() ?: ""
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            updateHistoryVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility()
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                showKeyboard()
            }
        }

        searchEditText.setOnClickListener {
            if (searchEditText.text.isNullOrEmpty()) {
                showKeyboard()
            }
        }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            updateHistoryVisibility()
        }

        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = searchAdapter

        historyAdapter = HistoryAdapter(
            emptyList(),
            onTrackClick = { track ->
                searchHistory.addTrack(track)
                updateHistoryVisibility()
            },
            onClearHistoryClick = {
                searchHistory.clearHistory()
                historyAdapter.clearHistory()
                updateHistoryVisibility()
            }
        )

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun updateHistoryVisibility() {
        val hasFocus = searchEditText.hasFocus()
        val isEmpty = searchEditText.text.isNullOrEmpty()
        val history = searchHistory.getHistory()

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

        currentSearchCall?.cancel()

        currentSearchCall = NetworkClient.itunesApi.search(query)
        currentSearchCall?.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    val tracks = searchResponse?.results ?: emptyList()

                    if (tracks.isNotEmpty()) {
                        showTracks(tracks)
                    } else {
                        showEmptyResults()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                if (!call.isCanceled) {
                    showError()
                }
            }
        })
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

        searchAdapter.updateTracks(tracks)
    }

    private fun showEmptyResults() {
        loadingProgressBar.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE

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

        currentSearchCall?.cancel()
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        searchEditText.setText(savedSearchQuery)
        clearButton.visibility = if (savedSearchQuery.isEmpty()) View.GONE else View.VISIBLE
        updateHistoryVisibility()
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"
    }
}