package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): Call<SearchResponse>
}