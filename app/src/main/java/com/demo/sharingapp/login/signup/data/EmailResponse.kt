package com.demo.sharingapp.login.signup.data

import com.demo.sharingapp.login.data.Data

data class EmailResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: String
)
