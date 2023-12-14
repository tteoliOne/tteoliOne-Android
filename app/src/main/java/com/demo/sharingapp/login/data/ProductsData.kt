package com.demo.sharingapp.login.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ProductsData(
    val productId: Long,
    val imageUrl: String,
    val title: String,
    val unitPrice: Int,
    val walkingDistance: Double,
    val walkingTime: Int,
    val totalLikes: Int,
    val liked: Boolean,
)

