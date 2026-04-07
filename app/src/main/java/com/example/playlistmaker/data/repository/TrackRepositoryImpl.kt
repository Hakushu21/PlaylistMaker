package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.network.TrackNetworkDto
import com.example.playlistmaker.data.dto.storage.TrackStorageDto
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
        call.enqueue(object : Callback<com.example.playlistmaker.data.dto.network.SearchResponseNetworkDto> {
            override fun onResponse(
                call: Call<com.example.playlistmaker.data.dto.network.SearchResponseNetworkDto>,
                response: Response<com.example.playlistmaker.data.dto.network.SearchResponseNetworkDto>
            ) {
                if (response.isSuccessful) {
                    val trackNetworkDtos = response.body()?.results ?: emptyList()
                    val tracks = trackNetworkDtos.map { mapNetworkToDomain(it) }
                    continuation.resume(Result.success(tracks))
                } else {
                    continuation.resume(Result.failure(IOException("Network error")))
                }
            }

            override fun onFailure(
                call: Call<com.example.playlistmaker.data.dto.network.SearchResponseNetworkDto>,
                t: Throwable
            ) {
                continuation.resume(Result.failure(t))
            }
        })
    }

    override fun saveSearchHistory(tracks: List<Track>) {
        val trackStorageDtos = tracks.map { mapDomainToStorage(it) }
        searchHistoryStorage.saveHistory(trackStorageDtos)
    }

    override fun getSearchHistory(): List<Track> {
        val trackStorageDtos = searchHistoryStorage.getHistory()
        return trackStorageDtos.map { mapStorageToDomain(it) }
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }

    override fun addTrackToHistory(track: Track) {
        val trackStorageDto = mapDomainToStorage(track)
        searchHistoryStorage.addTrack(trackStorageDto)
    }


    private fun mapNetworkToDomain(networkDto: TrackNetworkDto): Track {
        return Track(
            trackId = networkDto.trackId,
            trackName = networkDto.trackName,
            artistName = networkDto.artistName,
            trackTimeMillis = networkDto.trackTimeMillis,
            artworkUrl100 = networkDto.artworkUrl100,
            collectionName = networkDto.collectionName,
            releaseDate = networkDto.releaseDate,
            primaryGenreName = networkDto.primaryGenreName,
            country = networkDto.country,
            previewUrl = networkDto.previewUrl
        )
    }

    private fun mapDomainToStorage(track: Track): TrackStorageDto {
        return TrackStorageDto(
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

    private fun mapStorageToDomain(storageDto: TrackStorageDto): Track {
        return Track(
            trackId = storageDto.trackId,
            trackName = storageDto.trackName,
            artistName = storageDto.artistName,
            trackTimeMillis = storageDto.trackTimeMillis,
            artworkUrl100 = storageDto.artworkUrl100,
            collectionName = storageDto.collectionName,
            releaseDate = storageDto.releaseDate,
            primaryGenreName = storageDto.primaryGenreName,
            country = storageDto.country,
            previewUrl = storageDto.previewUrl
        )
    }
}