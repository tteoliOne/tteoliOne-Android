package com.demo.sharingapp.login.find_id.data

import com.google.gson.annotations.SerializedName

data class FindIdEmailVerifyData(
    @SerializedName("username")
    val username: String,
    @SerializedName("authCode")
    val authCode: String,
    @SerializedName("email")
    val email: String,
)
