package com.demo.sharingapp.domain.chat.chatroom.data

data class GetChatRoomInfoData(
    val id: String,
    val chatRoomNo: Long,
    val senderNo: Long,
    val senderName: String,
    val contentType: String,
    val content: String,
    val sendDate: Long,
    val readCount: Long,
    val mine: Boolean,

)
