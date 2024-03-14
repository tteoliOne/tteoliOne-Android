package com.demo.sharingapp.domain.other_profile.data

data class OtherProfileSimpleResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: OtherProfileSimpleData
)
