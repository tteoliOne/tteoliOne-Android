package com.demo.sharingapp.login.find_id.data

import com.google.gson.annotations.SerializedName

data class FindIdData(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
)
