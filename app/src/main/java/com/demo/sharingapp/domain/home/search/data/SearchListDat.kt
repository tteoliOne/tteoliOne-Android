package com.demo.sharingapp.domain.home.search.data

import com.demo.sharingapp.domain.home.data.PartProductContent

data class SearchListDat(
    val content:List<PartProductContent>,
    val pageable:SearchPageable,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
)
