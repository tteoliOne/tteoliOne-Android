package com.demo.sharingapp.login.data

import com.demo.sharingapp.data.AddProductResponseData

data class ProductsResponse(
    val success : Boolean,
    val code : Int,
    val message : String,
    val data: AddProductResponseData
)
