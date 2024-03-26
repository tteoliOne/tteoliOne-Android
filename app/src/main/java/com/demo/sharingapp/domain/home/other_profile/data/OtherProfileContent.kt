package com.demo.sharingapp.domain.home.other_profile.data

data class OtherProfileContent(
    val productId: Long,
    val imageUrl: String,
    val title: String,
    val unitPrice: Int,
    val walkingDistance: Double,
    val walkingTime: Int,
    val totalLikes: Int,
    val soldStatus: String,
    val likeId: Long,
    val liked: Boolean,
)
