package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackId")
    val trackId: Long,

    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String,

    @SerializedName("collectionName")
    val collectionName: String?,

    @SerializedName("releaseDate")
    val releaseDate: String?,

    @SerializedName("primaryGenreName")
    val primaryGenreName: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("previewUrl")
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