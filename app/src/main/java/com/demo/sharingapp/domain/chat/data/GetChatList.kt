package com.demo.sharingapp.domain.chat.data

data class GetChatList(
    val success : Boolean,     // 성공 여부 (실패시 false, 성공시 true)
    val code : Int,            // 상태 코드
    val message : String,      // 상태 메시지
    val data : List<GetChatListData> // 채팅 리스트 정보
)
