package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.storage.TrackStorageDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {
    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun getHistory(): List<TrackStorageDto> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<TrackStorageDto>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    fun addTrack(trackDto: TrackStorageDto) {
        val currentHistory = getHistory().toMutableList()

        currentHistory.removeAll { it.trackId == trackDto.trackId }
        currentHistory.add(0, trackDto)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeLast()
        }

        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    fun saveHistory(tracks: List<TrackStorageDto>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }
}