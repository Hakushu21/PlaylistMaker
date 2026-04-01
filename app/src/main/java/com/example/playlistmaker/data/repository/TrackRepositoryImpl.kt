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
        val trackDtos = tracks.map { mapToDto(it) }
        searchHistoryStorage.saveHistory(trackDtos)
    }

    override fun getSearchHistory(): List<Track> {
        val trackDtos = searchHistoryStorage.getHistory()
        return trackDtos.map { mapToDomain(it) }
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        val trackDto = mapToDto(track)
        searchHistoryStorage.addTrack(trackDto)
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

    private fun mapToDto(track: Track): TrackDto {
        return TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}