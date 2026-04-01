package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TrackRepositoryImpl(
    private val searchHistoryStorage: SearchHistoryStorage
) : TrackRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> = suspendCoroutine { continuation ->
        val call = NetworkClient.itunesApi.search(query)
        call.enqueue(object : Callback<com.example.playlistmaker.data.dto.SearchResponseDto> {
            override fun onResponse(
                call: Call<com.example.playlistmaker.data.dto.SearchResponseDto>,
                response: Response<com.example.playlistmaker.data.dto.SearchResponseDto>
            ) {
                if (response.isSuccessful) {
                    val trackDtos = response.body()?.results ?: emptyList()
                    val tracks = trackDtos.map { mapToDomain(it) }
                    continuation.resume(Result.success(tracks))
                } else {
                    continuation.resume(Result.failure(IOException("Network error")))
                }
            }

            override fun onFailure(
                call: Call<com.example.playlistmaker.data.dto.SearchResponseDto>,
                t: Throwable
            ) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        searchHistoryStorage.saveHistory(tracks)
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getHistory()
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }

    private fun mapToDomain(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }
}