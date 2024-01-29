package com.demo.sharingapp.domain.home.search.data

data class SearchContent(
    val productId: Long,
    val imageUrl: String,
    val title: String,
    val unitPrice: Int,
    val walkingDistance: Double,
    val walkingTime: Int,
    val soldStatus: String,
    val likeId: Long,
    val liked: Boolean,
    val totalLikes: Int,
)
