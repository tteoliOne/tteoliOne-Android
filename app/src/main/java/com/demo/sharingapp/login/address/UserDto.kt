package com.demo.sharingapp.login.address

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("common")
    val common: Common,
    @SerializedName("results")
    val results: User,

)
