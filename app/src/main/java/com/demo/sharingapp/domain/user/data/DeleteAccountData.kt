package com.demo.sharingapp.domain.user.data

import com.google.gson.annotations.SerializedName

data class DeleteAccountData(
    @SerializedName("authorizationCode")
    val authorizationCode : String
)
