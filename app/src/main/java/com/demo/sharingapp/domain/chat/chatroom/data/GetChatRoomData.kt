package com.demo.sharingapp.domain.chat.chatroom.data

data class GetChatRoomData(
    val loginId: String,                        // 자신의 loginId
    val productId : Long,                       // 상품Id
    val productImage : String,                  // 상품 대표이미지
    val sharePrice : Int,                       // 상품 공유가격
    val opponentNickname : String,              // 상대방 닉네임
    val soldStatus : String,                    // 상품 공유 상태
    val title : String,                         // 상품 제목
    val checkSeller : Boolean,                  // 자신이 판매자인지 아닌지
    val chatList: List<GetChatRoomInfoData>,    // 채팅 내역
    val opponentProfile: String,                // 상대방 프로필

)
