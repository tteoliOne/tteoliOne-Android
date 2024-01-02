package com.demo.sharingapp.utils

import android.Manifest

object Constants{
    const val TAG : String = "로그"
    const val ACCESS_TOKEN = "accessToken"
    const val REFRESH_TOKEN = "refreshToken"
    const val NICKNAME = "nickname"
    const val USER_ID = "userId"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val FIND_LATITUDE = "latitude1"
    const val FIND_LONGITUDE = "longitude1"
    const val SIGNUP_EMAIL = "email"
    const val SIGNUP_ID = "id"
    const val SIGNUP_NAME = "name"
    const val SIGNUP_PASSWORD = "password"
    const val PRODUCT_ID = "productId"
    const val DETAILED_LIKED = "detailedLiked"
    const val DETAILED_LIKED_POINT = "detailedLikedPoint"

    const val ACCESS_FINE_LOCATION_CODE = 200

    const val MOVE_DETAILED_CODE = 201



    //카메라를 호출하는 플래그
    const val FLAG_REQ_CAMERA = 101
}

object API {
    const val BASE_URL : String = "https://tteolione.store"

    const val KAKAO_TOKEN : String = "/api/users/kakao"

    const val EMAIL_URL : String = "/api/email/send/signup"

    const val EMAIL_VERIFY : String = "/api/email/verify/signup"

    const val PRODUCTS : String="/api/products"

    const val PRODUCTS_ME : String="/api/products/me"

    const val PRODUCTS_SIMPLE : String="/api/products/simple"

    const val SIGNUP : String="/api/users/signup"

    const val LOGIN : String="/api/users/login"

    const val REISSUE : String="/api/users/reissue"

    const val CHECK_ID : String="/api/users/check/login-id"

    const val CHECK_NICKNAME : String="/api/users/check/nickname"

    const val PRODUCT_LIKE : String="/api/products/{productId}/likes"

    const val FIND_ID : String="/api/users/find/login-id"

    const val FIND_ID_EMAIL_VERIFY : String="/api/users/verify/login-id"

    const val FIND_PASSWORD_EMAIL : String="/api/users/find/password"

    const val FIND_PASSWORD_EMAIL_VERIFY : String="/api/users/verify/password"

    const val FIND_PASSWORD_RESET : String="/api/users/reset/password"

    const val SAVE_PRODUCTS : String="/api/products/saved"

    const val DETAILED_PRODUCT : String="/api/products/{productId}"

    const val CHANGE_NICKNAME : String="/api/users/nickname"

    const val GET_PRODUCTS : String="/api/products/userId=&longitude=&latitude="

}