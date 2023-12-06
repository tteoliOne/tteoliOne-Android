package com.demo.sharingapp.data

data class SaveProductsData(
    val likedId : Long,
    val productId : Long,
    val productImage : String,
    val title : String,
    val soldStatus : String
)
