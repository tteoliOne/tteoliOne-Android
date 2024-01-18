package com.demo.sharingapp.domain.user.data

import com.google.gson.annotations.SerializedName

data class ChangeMyInfoData(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("intro")
    val intro: String,
)
