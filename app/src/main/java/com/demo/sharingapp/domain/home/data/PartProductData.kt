package com.demo.sharingapp.domain.home.data

data class PartProductData(
    val success: Boolean,
    val code : Int,
    val message: String,
    val data: PartProductListData
)
