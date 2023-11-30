package com.demo.sharingapp.login.signup.data

import com.google.gson.annotations.SerializedName

data class AuthCodeData(
    @SerializedName("authCode")
    val authCode: String,
    @SerializedName("email")
    val email: String

)
