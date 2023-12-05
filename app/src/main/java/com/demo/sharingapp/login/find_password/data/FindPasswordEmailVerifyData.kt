package com.demo.sharingapp.login.find_password.data

import com.google.gson.annotations.SerializedName

data class FindPasswordEmailVerifyData(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("authCode")
    val authCode: String,

)
