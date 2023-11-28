package com.demo.sharingapp.login.signup.data

import com.google.gson.annotations.SerializedName

data class SignupData(
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("password")
    val password: String,
)
