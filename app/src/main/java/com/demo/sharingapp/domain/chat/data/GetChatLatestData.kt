package com.demo.sharingapp.domain.chat.data

data class GetChatLatestData(
    val context : String, // 마지막 메세지 내용
    val sendAt : Long     // 마지막 메세지 보낸 시간
)
