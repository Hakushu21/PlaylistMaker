package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var emptyStateText: TextView

    private var searchQuery: String = ""

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchQuery = s?.toString() ?: ""
            clearButton.visibility = if (s.isNullOrEmpty()) TextView.GONE else TextView.VISIBLE
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupClickListeners()
        setupSearchField()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        emptyStateText = findViewById(R.id.empty_state_text)
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
        searchEditText.addTextChangedListener(searchTextWatcher)

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
        outState.putString("SEARCH_QUERY", searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedSearchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
        searchEditText.setText(savedSearchQuery)
        clearButton.visibility = if (savedSearchQuery.isEmpty()) TextView.GONE else TextView.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        searchEditText.removeTextChangedListener(searchTextWatcher)
    }
}