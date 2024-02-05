package com.demo.sharingapp.login.data

data class TokenResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: Data
)
