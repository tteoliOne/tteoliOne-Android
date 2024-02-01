package com.demo.sharingapp.login.data

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("loginId")
    val loginId: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("targetToken")
    val targetToken: String?
)
