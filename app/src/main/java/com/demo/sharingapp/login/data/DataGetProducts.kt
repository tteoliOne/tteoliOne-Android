package com.demo.sharingapp.login.data

data class DataGetProducts(
    val categoryId: Int,
    val categoryName: String,
    val products: List<ProductsData>,
)
