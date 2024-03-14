package com.demo.sharingapp.domain.other_profile.data

data class OtherProfileData(
    val content: List<OtherProfileContent>,
    val pageable: OtherProfilePageable,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
)
