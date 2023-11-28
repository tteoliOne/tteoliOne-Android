package com.demo.sharingapp.login.data

data class GetProductsResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: DataGetProductList
)
