package com.demo.sharingapp.login.data

data class Data(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
    val nickname: String,
)
