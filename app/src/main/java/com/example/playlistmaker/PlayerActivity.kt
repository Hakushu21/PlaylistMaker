package com.example.playlistmaker

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class PlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var artworkImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var trackDurationTextView: TextView
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var albumLayout: android.widget.LinearLayout
    private lateinit var yearLayout: android.widget.LinearLayout
    private lateinit var genreLayout: android.widget.LinearLayout
    private lateinit var countryLayout: android.widget.LinearLayout

    private var isPlaying = false
    private var currentPosition = 0
    private var totalDuration = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (isPlaying && currentPosition < totalDuration) {
                currentPosition += 1000
                updateProgress()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupClickListeners()

        val track = intent.getSerializableExtra("track") as? Track
        track?.let {
            displayTrackInfo(it)
            totalDuration = it.trackTimeMillis.toInt()
            updateTotalTime()
        }

        updatePlayPauseButton()
        updateFavoriteButton()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        artworkImageView = findViewById(R.id.artworkImageView)
        trackNameTextView = findViewById(R.id.trackNameTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        trackDurationTextView = findViewById(R.id.trackDurationTextView)
        addToPlaylistButton = findViewById(R.id.addToPlaylistButton)
        favoriteButton = findViewById(R.id.favoriteButton)
        playPauseButton = findViewById(R.id.playPauseButton)
        durationValue = findViewById(R.id.durationValue)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        albumLayout = findViewById(R.id.albumLayout)
        yearLayout = findViewById(R.id.yearLayout)
        genreLayout = findViewById(R.id.genreLayout)
        countryLayout = findViewById(R.id.countryLayout)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        addToPlaylistButton.setOnClickListener {
        }

        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun displayTrackInfo(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationTextView.text = track.getFormattedTime()
        durationValue.text = track.getFormattedTime()

        if (!track.collectionName.isNullOrEmpty()) {
            albumValue.text = track.collectionName
            albumLayout.visibility = android.view.View.VISIBLE
        } else {
            albumLayout.visibility = android.view.View.GONE
        }

        track.getReleaseYear()?.let {
            yearValue.text = it
            yearLayout.visibility = android.view.View.VISIBLE
        } ?: run {
            yearLayout.visibility = android.view.View.GONE
        }

        if (!track.primaryGenreName.isNullOrEmpty()) {
            genreValue.text = track.primaryGenreName
            genreLayout.visibility = android.view.View.VISIBLE
        } else {
            genreLayout.visibility = android.view.View.GONE
        }

        if (!track.country.isNullOrEmpty()) {
            countryValue.text = track.country
            countryLayout.visibility = android.view.View.VISIBLE
        } else {
            countryLayout.visibility = android.view.View.GONE
        }

        val isDarkTheme = isDarkThemeEnabled()
        val placeholderRes = if (isDarkTheme) {
            R.drawable.ic_album_placeholder_312_dark
        } else {
            R.drawable.ic_album_placeholder_312_light
        }

        if (!track.artworkUrl100.isNullOrEmpty()) {
            val artworkUrl512 = track.getArtworkUrl512()
            val cornerRadiusPx = dpToPx(8)

            Glide.with(this)
                .load(artworkUrl512)
                .placeholder(placeholderRes)
                .error(placeholderRes)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadiusPx)))
                .into(artworkImageView)
        } else {
            artworkImageView.setImageResource(placeholderRes)
        }
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying
        updatePlayPauseButton()

        if (isPlaying) {
            handler.postDelayed(updateProgressRunnable, 1000)
        } else {
            handler.removeCallbacks(updateProgressRunnable)
            currentPosition = 0
        }
    }

    private fun updatePlayPauseButton() {
        val isDarkTheme = isDarkThemeEnabled()

        if (isPlaying) {
            val pauseIconRes = if (isDarkTheme) {
                R.drawable.ic_pause_dark_84
            } else {
                R.drawable.ic_pause_light_84
            }
            playPauseButton.setImageResource(pauseIconRes)
        } else {
            val playIconRes = if (isDarkTheme) {
                R.drawable.ic_play_dark_84
            } else {
                R.drawable.ic_play_light_84
            }
            playPauseButton.setImageResource(playIconRes)
        }
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        val favoriteIconRes = if (isFavorite) {
            R.drawable.ic_favorite_active_51
        } else {
            R.drawable.ic_favorite_51
        }
        favoriteButton.setImageResource(favoriteIconRes)
    }

    private fun updateProgress() {
        val minutes = currentPosition / 1000 / 60
        val seconds = currentPosition / 1000 % 60
        trackDurationTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateTotalTime() {
        val minutes = totalDuration / 1000 / 60
        val seconds = totalDuration / 1000 % 60
        trackDurationTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun isDarkThemeEnabled(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgressRunnable)
    }
}