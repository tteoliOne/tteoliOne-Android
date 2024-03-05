package com.demo.sharingapp.domain.home.data

data class PartProductPageable(
    //val sort: SortData,
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean,
    val paged: Boolean
)
