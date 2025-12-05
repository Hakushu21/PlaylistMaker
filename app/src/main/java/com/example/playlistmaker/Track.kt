package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
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
) {
    fun getFormattedTime(): String {
        return try {
            val minutes = trackTimeMillis / 1000 / 60
            val seconds = trackTimeMillis / 1000 % 60
            String.format("%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            "00:00"
        }
    }
}