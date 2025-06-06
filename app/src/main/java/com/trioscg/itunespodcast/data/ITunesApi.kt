package com.trioscg.itunespodcast.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search")
    suspend fun searchPodcasts(
        @Query("term") term: String,
        @Query("media") media: String = "podcast"
    ): PodcastResponse
}