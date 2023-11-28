package com.demo.sharingapp.login.data

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)
