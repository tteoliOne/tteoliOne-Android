package com.demo.sharingapp.domain.chat.chatroom.data

import com.google.gson.annotations.SerializedName

data class PostReportBody(
    @SerializedName("content")
    val content: String?,
    @SerializedName("reporteeId")
    val reporteeId: String?,
)
