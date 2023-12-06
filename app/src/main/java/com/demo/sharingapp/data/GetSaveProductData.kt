package com.demo.sharingapp.data

data class GetSaveProductData(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: SaveProductsListData
)
