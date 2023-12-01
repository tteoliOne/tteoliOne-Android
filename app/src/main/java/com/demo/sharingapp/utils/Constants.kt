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
    const val SIGNUP_EMAIL = "email"
    const val SIGNUP_ID = "id"
    const val SIGNUP_NAME = "name"
    const val SIGNUP_PASSWORD = "password"

    const val ACCESS_FINE_LOCATION_CODE = 200



    //카메라를 호출하는 플래그
    const val FLAG_REQ_CAMERA = 101
}

object API {
    const val BASE_URL : String = "https://tteolione.store"

    const val KAKAO_TOKEN : String = "/api/users/kakao"

    const val EMAIL_URL : String = "/api/email/send/signup"

    const val EMAIL_VERIFY : String = "/api/email/verify/signup"

    const val PRODUCTS : String="/api/products"

    const val SIGNUP : String="/api/users/signup"

    const val LOGIN : String="/api/users/login"

    const val REISSUE : String="/api/users/reissue"

    const val CHECK_ID : String="/api/users/check/login-id"

    const val CHECK_NICKNAME : String="/api/users/check/nickname"

    const val PRODUCT_LIKE : String="/api/products/{productId}/likes"

    const val FIND_ID : String="/api/users/find/login-id"

    const val FIND_ID_EMAIL_VERIFY : String="/api/users/verify/login-id"

    const val GET_PRODUCTS : String="/api/products/userId=&longitude=&latitude="

}