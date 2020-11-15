package com.yennsarah.data

import kotlinx.serialization.Serializable

@Serializable
data class RedditNewsResponse(val data: RedditDataResponse)

@Serializable
data class RedditDataResponse(
    val children: List<RedditChildrenResponse>,
    val after: String?,
    val before: String?
)

@Serializable
class RedditChildrenResponse(val data: RedditNewsDataResponse)

@Serializable
class RedditNewsDataResponse(
    val author: String,
    val title: String,
    val num_comments: Int,
    val created: Double,
    val thumbnail: String,
    val url: String?
)