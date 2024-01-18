package com.demo.sharingapp.domain.user.data

data class MyInfoResponse(
    val success : Boolean,
    val code: Int,
    val message: String,
    val data: MyInfoData
)
