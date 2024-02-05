package com.demo.sharingapp.domain.chat.chatroom.data

import com.google.gson.annotations.SerializedName

data class ChatSendCallBack(
    @SerializedName("id")
    val id: String?,
    @SerializedName("chatRoomNo")
    val chatRoomNo: Long,
    @SerializedName("contentType")
    val contentType: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("senderName")
    val senderName: String,
    @SerializedName("senderNo")
    val senderNo: Long,
    @SerializedName("productNo")
    val productNo: Long,
    @SerializedName("sendTime")
    val sendTime: Long,
    @SerializedName("readCount")
    val readCount: Int,
    @SerializedName("senderLoginId")
    val senderLoginId: String,
)
