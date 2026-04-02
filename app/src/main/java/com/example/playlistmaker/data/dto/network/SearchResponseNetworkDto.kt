package com.example.playlistmaker.data.dto.network

import com.google.gson.annotations.SerializedName

data class SearchResponseNetworkDto(
    @SerializedName("resultCount")
    val resultCount: Int,

    @SerializedName("results")
    val results: List<TrackNetworkDto>
)