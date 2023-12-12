package com.demo.sharingapp.domain.home.part.data

data class DetailedProductResponseData(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: DetailedProductData
)
