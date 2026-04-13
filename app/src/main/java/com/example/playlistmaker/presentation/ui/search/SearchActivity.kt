package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.adapters.HistoryAdapter
import com.example.playlistmaker.presentation.adapters.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.example.playlistmaker.presentation.ui.search.SearchScreenState.SearchState
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

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

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: HistoryAdapter

    private var isClickAllowed = true
    private val clickDebounceDelay = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        setupWindowInsets()
        initViews()
        setupClickListeners()
        setupSearchField()
        setupRecyclerViews()
        observeViewModel()
    }

    private fun enableEdgeToEdge() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
            searchEditText.setText("")
            hideKeyboard()
            searchEditText.clearFocus()
        }

        retryButton.setOnClickListener {
            viewModel.onRetryClicked()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.GONE
                viewModel.onQueryTextChanged(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchFieldFocused(hasFocus)
            if (hasFocus && searchEditText.text.isNullOrEmpty()) {
                showKeyboard()
            }
        }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
            }
        }

        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = searchAdapter

        historyAdapter = HistoryAdapter(
            emptyList(),
            onTrackClick = { track ->
                if (clickDebounce()) {
                    viewModel.onTrackClicked(track)
                }
            },
            onClearHistoryClick = {
                viewModel.onClearHistoryClicked()
            }
        )

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(this) { state ->
            hideAllViews()

            when (val searchState = state.searchState) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showTracks(searchState.tracks)
                is SearchState.Empty -> showEmptyResults()
                is SearchState.Error -> showError()
                is SearchState.History -> showHistory(searchState.tracks)
                is SearchState.StartSearch -> showStartSearch()
            }

            if (searchEditText.text.toString() != state.queryText) {
                searchEditText.setText(state.queryText)
                searchEditText.setSelection(state.queryText.length)
            }

            state.navigateToPlayer?.let { track ->
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
                viewModel.onNavigateToPlayerHandled()
            }
        }
    }

    private fun hideAllViews() {
        loadingProgressBar.visibility = View.GONE
        tracksRecyclerView.visibility = View.GONE
        startSearchLayout.visibility = View.GONE
        nothingFoundLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
    }

    private fun showTracks(tracks: List<Track>) {
        tracksRecyclerView.visibility = View.VISIBLE
        searchAdapter.updateTracks(tracks)
    }

    private fun showEmptyResults() {
        nothingFoundLayout.visibility = View.VISIBLE
        updateNothingFoundView()
    }

    private fun showError() {
        errorLayout.visibility = View.VISIBLE
        updateErrorView()
    }

    private fun showHistory(tracks: List<Track>) {
        historyLayout.visibility = View.VISIBLE
        historyAdapter.updateTracks(tracks)
    }

    private fun showStartSearch() {
        startSearchLayout.visibility = View.VISIBLE
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                { isClickAllowed = true },
                clickDebounceDelay
            )
        }
        return current
    }

    private fun showKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}