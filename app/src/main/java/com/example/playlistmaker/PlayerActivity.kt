package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Locale

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
    private lateinit var albumLabel: TextView
    private lateinit var yearLabel: TextView
    private lateinit var genreLabel: TextView
    private lateinit var countryLabel: TextView
    private lateinit var currentTimeTextView: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable? = null

    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        setupWindowInsets()
        initViews()
        setupClickListeners()

        currentTrack = intent.getSerializableExtra("track") as? Track
        currentTrack?.let {
            displayTrackInfo(it)
            setupMediaPlayer(it.previewUrl)
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

        albumLabel = findViewById(R.id.albumLabel)
        yearLabel = findViewById(R.id.yearLabel)
        genreLabel = findViewById(R.id.genreLabel)
        countryLabel = findViewById(R.id.countryLabel)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            releaseMediaPlayer()
            finish()
        }

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        addToPlaylistButton.setOnClickListener {
        }

        favoriteButton.setOnClickListener {
        }
    }

    private fun setupMediaPlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) {
            playPauseButton.isEnabled = false
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                prepareAsync()

                setOnPreparedListener {
                    playPauseButton.isEnabled = true
                }

                setOnCompletionListener {
                    onPlaybackComplete()
                }

                setOnErrorListener { _, what, extra ->
                    playPauseButton.isEnabled = false
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            playPauseButton.isEnabled = false
        }
    }

    private fun togglePlayPause() {
        if (mediaPlayer == null) return

        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
                this@PlayerActivity.isPlaying = true
                playPauseButton.setImageResource(R.drawable.ic_pause_84)
                showCurrentTime()
                startProgressUpdate()
            }
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                this@PlayerActivity.isPlaying = false
                playPauseButton.setImageResource(R.drawable.ic_play_84)
                stopProgressUpdate()
                showTotalDuration()
            }
        }
    }

    private fun onPlaybackComplete() {
        isPlaying = false
        playPauseButton.setImageResource(R.drawable.ic_play_84)
        stopProgressUpdate()
        showTotalDuration()

        mediaPlayer?.apply {
            seekTo(0)
        }
    }

    private fun startProgressUpdate() {
        updateProgressRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val currentPosition = player.currentPosition
                        updateCurrentTime(currentPosition)
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }
        handler.post(updateProgressRunnable!!)
    }

    private fun stopProgressUpdate() {
        updateProgressRunnable?.let { handler.removeCallbacks(it) }
        updateProgressRunnable = null
    }

    private fun updateCurrentTime(positionMs: Int) {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        currentTimeTextView.text = formatter.format(positionMs)
    }

    private fun showCurrentTime() {
        trackDurationTextView.visibility = View.GONE
        currentTimeTextView.visibility = View.VISIBLE
    }

    private fun showTotalDuration() {
        trackDurationTextView.visibility = View.VISIBLE
        currentTimeTextView.visibility = View.GONE
    }

    private fun releaseMediaPlayer() {
        stopProgressUpdate()
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isPlaying = false
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    private fun displayTrackInfo(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName

        val formattedTime = track.getFormattedTime()
        trackDurationTextView.text = formattedTime
        durationValue.text = formattedTime

        showTotalDuration()

        if (!track.collectionName.isNullOrEmpty()) {
            albumValue.text = track.collectionName
            albumLabel.visibility = View.VISIBLE
            albumValue.visibility = View.VISIBLE
        } else {
            albumLabel.visibility = View.GONE
            albumValue.visibility = View.GONE
        }

        track.getReleaseYear()?.let {
            yearValue.text = it
            yearLabel.visibility = View.VISIBLE
            yearValue.visibility = View.VISIBLE
        } ?: run {
            yearLabel.visibility = View.GONE
            yearValue.visibility = View.GONE
        }

        if (!track.primaryGenreName.isNullOrEmpty()) {
            genreValue.text = track.primaryGenreName
            genreLabel.visibility = View.VISIBLE
            genreValue.visibility = View.VISIBLE
        } else {
            genreLabel.visibility = View.GONE
            genreValue.visibility = View.GONE
        }

        if (!track.country.isNullOrEmpty()) {
            countryValue.text = track.country
            countryLabel.visibility = View.VISIBLE
            countryValue.visibility = View.VISIBLE
        } else {
            countryLabel.visibility = View.GONE
            countryValue.visibility = View.GONE
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

    private fun isDarkThemeEnabled(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}