package com.demo.sharingapp.domain.home.part.data

data class DetailedProductData(
    val productId: Long,
    val images: Array<String>,
    val sellerProfile: String,
    val receipt: String,
    val sellerNickname: String,
    val title: String,
    val buyDate: String,
    val likeCount: Int,
    val buyCount: Int,
    val buyPrice: Int,
    val shareCount: Int,
    val sharePrice: Int,
    val description: String,
    val longitude: Double,
    val latitude: Double,
    val likeId: Long,
    val checkLiked: Boolean,
    val checkOwner: Boolean,
    val soldStatus: String,
)
