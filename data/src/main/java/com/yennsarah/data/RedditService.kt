package com.yennsarah.data

import retrofit2.http.GET
import retrofit2.http.Query

interface RedditService {
    @GET("/top.json")
    suspend fun getTop(
        @Query("after") after: String,
        @Query("limit") limit: String
    ): RedditNewsResponse
}