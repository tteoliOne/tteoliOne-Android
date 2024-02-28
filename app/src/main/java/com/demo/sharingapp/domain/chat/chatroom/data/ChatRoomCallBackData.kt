package com.demo.sharingapp.domain.chat.chatroom.data

data class ChatRoomCallBackData(
    val chatRoomNo : Int,
    val contentType : String,
    val content : String,
    val senderName: String,
    val senderNo: Int,
    val productNo: Int,
    val sendTime: Long,
    val readCount : Int
)
