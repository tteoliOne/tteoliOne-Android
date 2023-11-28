package com.demo.sharingapp.login.data

import com.google.gson.annotations.SerializedName

data class AccessTokenRequest(
    @SerializedName("accessToken")
    val accessToken: String
)
