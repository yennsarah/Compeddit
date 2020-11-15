package com.yennsarah.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class RedditApi {
    private val redditApi: RedditService by lazy {
        retrofit.create(RedditService::class.java)
    }

    private val okHttpClient by lazy {
        OkHttpClient()
    }

    private val retrofit by lazy {
        val contentType = MediaType.parse("application/json")!!
        val converter = Json { ignoreUnknownKeys = true }.asConverterFactory(contentType)
        Retrofit.Builder()
            .baseUrl("https://www.reddit.com")
            .addConverterFactory(converter)
            // .client(okHttpClient)
            .build()
    }

    suspend fun getNews(after: String, limit: String): RedditNewsResponse {
        return redditApi.getTop(after, limit)
    }
}
