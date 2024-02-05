package com.demo.sharingapp.domain.chat.data

data class GetChatListData(
    val chatNo : Long,                     // 채팅방 Id
    val createMember : Long,               // 연락하기 버튼 누른사람Id
    val joinMember : Long,                 // 상품 게시글 올린 사람Id
    val productNo : Long,                  // 상품Id
    val productTitle : String,             // 상품 제목
    val regDate : Long,                    // 채팅방 생성시간
    val participant : GetChatUserData,     // 참여자 정보
    val latestMessage : GetChatLatestData, // 마지막 메세지 정보
    val unReadCount : Long                 // 읽지 않는 채팅갯수
)
