package com.demo.sharingapp.domain.chat.chatroom.data

import com.google.gson.annotations.SerializedName

data class CreateChatRoomData(
    @SerializedName("productNo")
    val productNo: Long,
)
