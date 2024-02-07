package com.demo.sharingapp.domain.chat.chatroom.data

data class GetChatRoomResponse(
    val success : Boolean,
    val code : Int,
    val message: String,
    val data : GetChatRoomData
)
