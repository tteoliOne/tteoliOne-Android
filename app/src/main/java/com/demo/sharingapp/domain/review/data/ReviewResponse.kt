package com.demo.sharingapp.domain.review.data

data class ReviewResponse(
    val success : Boolean,
    val code : Int,
    val message : String,
    val data : List<ReviewResponseData>,
)
