package com.demo.sharingapp.domain.chat.chatroom.data

data class GetChatRoomData(
    val loginId: String,
    val productId : Long,
    val productImage : String,
    val sharePrice : Int,
    val opponentNickname : String,
    val soldStatus : String,
    val title : String,
    val checkSeller : Boolean,
    val chatList: List<GetChatRoomInfoData>,
    val opponentProfile: String

)
