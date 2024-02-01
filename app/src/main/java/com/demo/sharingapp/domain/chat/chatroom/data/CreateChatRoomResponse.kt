package com.demo.sharingapp.domain.chat.chatroom.data

data class CreateChatRoomResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: CreateChatRoomResponseData
)