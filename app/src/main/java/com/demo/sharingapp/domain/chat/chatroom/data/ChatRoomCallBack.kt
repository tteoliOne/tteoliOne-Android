package com.demo.sharingapp.domain.chat.chatroom.data

import com.demo.sharingapp.login.data.Data

data class ChatRoomCallBack(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: ChatRoomCallBackData,

)
