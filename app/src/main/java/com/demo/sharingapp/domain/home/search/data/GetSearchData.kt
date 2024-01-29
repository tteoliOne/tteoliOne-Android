package com.demo.sharingapp.domain.home.search.data

import com.demo.sharingapp.domain.home.data.PartProductListData

data class GetSearchData(
    val success: Boolean,
    val code : Int,
    val message: String,
    val data: SearchData
)
