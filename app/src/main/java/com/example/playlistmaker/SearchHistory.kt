package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson = Gson()
) {
    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()

        // Удаляем старую запись этого трека, если она есть
        currentHistory.removeAll { it.trackId == track.trackId }

        // Добавляем новый трек в начало списка
        currentHistory.add(0, track)

        // Ограничиваем размер истории
        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeLast()
        }

        // Сохраняем обновленную историю
        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }
}