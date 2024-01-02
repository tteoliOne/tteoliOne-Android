package com.demo.sharingapp.domain.home.data

data class PartProductContent(
    val productId: Long,
    val imageUrl: String,
    val title: String,
    val unitPrice: Int,
    val walkingDistance: Double,
    val walkingTime: Int,
    var totalLikes: Int,
    val likeId: Long,
    var liked: Boolean,
    var isClamped: Boolean = false,
    var isCheckVisible: Boolean = false
)
