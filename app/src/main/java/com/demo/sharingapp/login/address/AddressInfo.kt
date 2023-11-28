package com.demo.sharingapp.login.address

import com.google.gson.annotations.SerializedName

data class AddressInfo(
    @SerializedName("bdNm")
    val bdNm: String,

    @SerializedName("roadAddrPart1")
    val roadAddrPart1: String,

)
