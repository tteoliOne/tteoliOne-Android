package com.demo.sharingapp.utils

import android.Manifest
import retrofit2.http.DELETE

object Constants{
    const val TAG : String = "로그"
    const val ACCESS_TOKEN = "accessToken"
    const val LOGIN_TYPE = "loginType"
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
    const val SIGNUP_NICKNAME = "nickname"
    const val SIGNUP_PASSWORD = "password"
    const val PRODUCT_ID = "productId"
    const val USER_PROFILE = "profile"
    const val PRODUCT_CATEGORY_ID = "productId"
    const val KAKAO_TOKEN = "kaKaoToken"
    const val SELLER_ID = "sellerId"
    const val NOTIFY_STATE = "notify_state"

    const val PRODUCT_TITLE = "productTitle"
    const val PRODUCT_BUY_PRICE = "productBuyPrice"
    const val PRODUCT_BUY_COUNT = "productBuyCount"
    const val PRODUCT_SHARE_PRICE = "productSharePrice"
    const val PRODUCT_SHARE_COUNT = "productShareCount"
    const val PRODUCT_PRODUCT_ID = "productProductId"
    const val PRODUCT_BUY_YEAR = "productBuyYear"
    const val PRODUCT_BUY_MONTH = "productBuyMonth"
    const val PRODUCT_BUY_DAY = "productBuyDay"
    const val PRODUCT_DESCRIPTION = "productDescription"
    const val PRODUCT_LATITUDE = "productLatitude"
    const val PRODUCT_LONGITUDE = "productLongitude"
    const val PRODUCT_IMAGE = "productImage"
    const val PRODUCT_RECEIPT_IMAGE = "productReceiptImage"
    const val PRODUCT_TYPE = "productType"
    const val SEARCH_RECODE = "searchRecode"
    const val CHATROOM_NUMBER = "chatroomNumber"
    const val PUSH_MESSAGE = "pushMessage"


    const val ACCESS_FINE_LOCATION_CODE = 200

    const val ACCESS_FINE_CAMERA_CODE = 204

    const val MOVE_DETAILED_CODE = 201

    const val MOVE_MODIFY_CODE = 202

    const val MOVE_CHAT_CODE = 203

    //카메라를 호출하는 플래그
    const val FLAG_REQ_CAMERA = 104
}

object API {
    const val BASE_URL : String = "https://tteolione.store"
//    const val BASE_URL : String = "http://118.41.215.56:8081"

    const val BASE_URL_CHAT : String = "ws://118.41.215.56:8080/ws-stomp"

    const val KAKAO_TOKEN : String = "/api/users/kakao"

    const val KAKAO_TOKEN_PROFILE : String = "/api/users/kakao/profile"

    const val EMAIL_URL : String = "/api/email/send/signup"

    const val EMAIL_VERIFY : String = "/api/email/verify/signup"

    const val PRODUCTS : String="/api/products"

    const val PRODUCTS_MODIFY : String="/api/products/{productId}"

    const val PRODUCTS_ME : String="/api/products/me"

    const val SEARCH : String="/api/search"

    const val PRODUCTS_SIMPLE : String="/api/products/simple"

    const val SIGNUP : String="/api/users/signup"

    const val LOGIN : String="/api/users/login"

    const val REISSUE : String="/api/users/reissue"

    const val CHECK_ID : String="/api/users/check/login-id"

    const val CHECK_NICKNAME : String="/api/users/check/nickname"

    const val PRODUCT_LIKE : String="/api/products/{productId}/likes"

    const val CHAT_ROOM : String="/api/chatRoom"

    const val FIND_ID : String="/api/users/find/login-id"

    const val FIND_ID_EMAIL_VERIFY : String="/api/users/verify/login-id"

    const val FIND_PASSWORD_EMAIL : String="/api/users/find/password"

    const val FIND_PASSWORD_EMAIL_VERIFY : String="/api/users/verify/password"

    const val FIND_PASSWORD_RESET : String="/api/users/reset/password"

    const val CHANGE_PASSWORD_RESET : String="/api/users/change/password"

    const val SAVE_PRODUCTS : String="/api/products/saved"

    const val GET_CHAT_LIST : String="/api/chatRoom"

    const val MY_INFO : String="/api/users"

    const val CHATROOM_DATA : String="/api/chatRoom/{roomNo}"

    const val CHAT_SEND_CALLBACK : String="/api/chatRoom/notification"

    const val DETAILED_PRODUCT : String="/api/products/{productId}"

    const val CHANGE_NICKNAME : String="/api/users/nickname"

    const val CHANGE_MY_INFO : String="/api/users"

    // 공유 요청
    const val PRODUCT_REQUEST : String="/api/products/{productId}/chatRoom/{chatRoomId}/request"

    // 로그아웃
    const val LOGOUT : String="/api/users/logout"

    // 회원탈퇴
    const val DELETE_ACCOUNT : String="/api/users/{userId}"

    // 상대방 프로필 간단 조회
    const val GET_OTHER_PROFILE_SIMPLE : String=" /api/users/{userId}/simple"

    // 상대방 프로필 조회
    const val GET_OTHER_PROFILE : String=" /api/products/users/{userId}"

    // 상품 공유 요청 거절
    const val PUT_PRODUCT_REJECT : String="/api/products/{productId}/chatRoom/{chatRoomId}/reject"

    // 상품 공유 요청 승인
    const val PUT_PRODUCT_APPROVE : String="/api/products/{productId}/chatRoom/{chatRoomId}/approve"

    // 상품 공유 후기 쓰기
    const val POST_PRODUCT_REVIEW : String="/api/products/{productId}/review"

    // 신고하기
    const val POST_REPORT : String="/api/reports/{reportType}/{id}"

    // 내정보-저장글 목록
    const val GET_MY_SAVE_PRODUCT : String="/api/products/me/saved"

    // 후기 정보 가져오기
    const val GET_REVIEW : String="/api/reviews/{userId}"




    // 채팅 _ 방 떠나기
    const val CHATROOM_LEAVE : String="/api/chatRoom/{chatRoomId}"

    const val LEAVE_CHATROOM : String="/api/chatRoom/{chatRoomId}"


    const val GET_PRODUCTS : String="/api/products/userId=&longitude=&latitude="

}