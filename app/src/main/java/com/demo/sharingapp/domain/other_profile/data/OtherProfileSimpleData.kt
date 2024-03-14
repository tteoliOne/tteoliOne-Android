package com.demo.sharingapp.domain.other_profile.data

data class OtherProfileSimpleData(
    val profile: String,                // 상대방 프로필url
    val nickname: String,               // 상대방 닉네임
    val intro: String,                  // 상대방 자기소개
    val newProductCount: Int,           // 판매중인 상품 갯수
    val soldOutProductCount: Int,       // 판매완료 상품 갯수
    val reviewCount: Int,               // 리뷰 갯수
    val ddabongScore: Double            // 따봉 점수
)
