package com.demo.sharingapp.domain.other_profile.data

data class OtherProfileResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: OtherProfileData
)
