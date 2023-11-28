package com.demo.sharingapp.login.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Products(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("categoryId")
    val categoryId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("buyPrice")
    val buyPrice: Int,

    @SerializedName("buyCount")
    val buyCount: Int,

    @SerializedName("sharePrice")
    val sharePrice: Int,

    @SerializedName("shareCount")
    val shareCount: Int,

    @SerializedName("buyDate")
    val buyDate: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("latitude")
    val latitude: Double,

    )
