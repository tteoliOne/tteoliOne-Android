package com.demo.sharingapp.login.data

import com.google.gson.annotations.SerializedName

data class LoginTokenData(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("targetToken")
    val targetToken: String?
)
