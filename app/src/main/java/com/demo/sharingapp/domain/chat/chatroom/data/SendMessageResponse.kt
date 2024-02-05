package com.demo.sharingapp.domain.chat.chatroom.data

data class SendMessageResponse(
    val id : String,
    val chatRoomNo : Long,
    val contentType : String,
    val content : String,
    val senderName : String,
    val senderNo : Long,
    val productNo : Long,
    val sendTime : Long,
    val readCount : Int,
    val senderLoginId : String,

)
