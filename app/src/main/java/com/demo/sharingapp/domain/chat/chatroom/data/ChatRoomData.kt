package com.demo.sharingapp.domain.chat.chatroom.data

data class ChatRoomData(
    val profile: String,
    val nickname: String,
    val title: String,
    val description: String,
    val burPrice: Int,
    val productImage: String,
    val chatType: Int,
)
