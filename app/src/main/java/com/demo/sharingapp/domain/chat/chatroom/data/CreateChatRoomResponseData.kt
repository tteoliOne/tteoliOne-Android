package com.demo.sharingapp.domain.chat.chatroom.data

import java.time.LocalDate
import java.time.LocalDateTime

data class CreateChatRoomResponseData(
    val chatId: Long,
    val createMember: Long,
    val joinMember: Long,
    val productNo: Long,
    val regDate: String,
)
