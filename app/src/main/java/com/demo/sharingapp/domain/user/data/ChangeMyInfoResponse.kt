package com.demo.sharingapp.domain.user.data

data class ChangeMyInfoResponse(
    val success : Boolean,
    val code : Int,
    val message : String,
    val data: String
)
