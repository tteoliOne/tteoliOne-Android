package com.demo.sharingapp.domain.review.data

import java.time.LocalDate
import java.time.LocalDateTime

data class ReviewResponseData(
    val productId: Long,
    val reviewId: Long,
    val writer: String,
    val content: String,
    val ddabongScore: Int,
)
