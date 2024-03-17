package com.demo.sharingapp.domain.chat.chatroom.data

import com.google.gson.annotations.SerializedName

data class PostReviewData(
    @SerializedName("content")
    val content: String,
    @SerializedName("ddabong")
    val ddabong: Long,
)
