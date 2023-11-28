package com.demo.sharingapp.login.address

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("juso")
    val juso: List<AddressInfo>,
)
