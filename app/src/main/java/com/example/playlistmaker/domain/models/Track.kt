package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Serializable {
    fun getFormattedTime(): String {
        return try {
            val minutes = trackTimeMillis / 1000 / 60
            val seconds = trackTimeMillis / 1000 % 60
            String.format("%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            "00:00"
        }
    }

    fun getReleaseYear(): String? {
        return try {
            releaseDate?.substring(0, 4)
        } catch (e: Exception) {
            null
        }
    }

    fun getArtworkUrl512(): String {
        return artworkUrl100.replace("100x100bb", "512x512bb")
    }
}