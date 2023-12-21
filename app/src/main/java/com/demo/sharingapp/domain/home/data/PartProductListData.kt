package com.demo.sharingapp.domain.home.data

data class PartProductListData(
    val content: List<PartProductContent>,
    val pageable: PartProductPageable,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean
)
