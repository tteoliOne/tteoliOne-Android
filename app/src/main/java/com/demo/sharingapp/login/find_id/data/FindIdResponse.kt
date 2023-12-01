package com.demo.sharingapp.login.find_id.data

data class FindIdResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: LonginId
)
