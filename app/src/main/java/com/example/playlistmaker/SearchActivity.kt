package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var emptyStateText: TextView
    private lateinit var tracksRecyclerView: RecyclerView

    private var searchQuery: String = ""
    private val trackAdapter = TrackAdapter(createSampleTracks())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupClickListeners()
        setupSearchField()
        setupRecyclerView()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        emptyStateText = findViewById(R.id.empty_state_text)
        tracksRecyclerView = findViewById(R.id.tracks_recycler_view)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            clearSearch()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchField() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            searchQuery = text?.toString() ?: ""
            clearButton.visibility = if (text.isNullOrEmpty()) TextView.GONE else TextView.VISIBLE
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
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

    private fun setupRecyclerView() {
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            hideKeyboard()
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        searchEditText.setText(savedSearchQuery)
        clearButton.visibility = if (savedSearchQuery.isEmpty()) TextView.GONE else TextView.VISIBLE
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY"

        private fun createSampleTracks(): List<Track> {
            return listOf(
                Track(
                    "Smells Like Teen Spirit",
                    "Nirvana",
                    "5:01",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Billie Jean",
                    "Michael Jackson",
                    "4:35",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
                ),
                Track(
                    "Stayin' Alive",
                    "Bee Gees",
                    "4:10",
                    "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
                ),
                Track(
                    "Whole Lotta Love",
                    "Led Zeppelin",
                    "5:33",
                    "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
                ),
                Track(
                    "Sweet Child O'Mine",
                    "Guns N' Roses",
                    "5:03",
                    "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
                )
            )
        }
    }
}