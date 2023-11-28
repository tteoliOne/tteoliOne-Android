package com.demo.sharingapp.login.data

data class ReissueData(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: ReissueTokenData
)

data class ReissueTokenData(
    val accessToken: String,
    val refreshToken: String
)
