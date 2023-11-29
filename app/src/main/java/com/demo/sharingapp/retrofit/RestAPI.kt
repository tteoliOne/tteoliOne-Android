package com.demo.sharingapp.retrofit

import com.demo.sharingapp.login.data.*
import com.demo.sharingapp.login.signup.data.*
import com.demo.sharingapp.utils.API
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RestAPI {

    // 이메일 보내기
    @POST(API.EMAIL_URL) // Replace with your API endpoint
    fun postEmailData(@Body email: EmailData): Call<EmailResponse>

    // 회원가입 정보 보내기
    @POST(API.SIGNUP) // Replace with your API endpoint
    fun postSignupData(@Body signup: SignupData): Call<EmailResponse>

    // 아이디 중복 확인 보내기
    @POST(API.CHECK_ID) // Replace with your API endpoint
    fun postCheckId(@Body loginId: IdData): Call<EmailResponse>

    // 닉네임 중복 확인 보내기
    @POST(API.CHECK_NICKNAME) // Replace with your API endpoint
    fun postCheckNickname(@Body nicknameData: NicknameData): Call<EmailResponse>

    // 로그인 정보 보내기
    @POST(API.LOGIN) // Replace with your API endpoint
    fun postLoginData(@Body loginData: LoginData): Call<TokenResponse>

    // 토큰 재발행 정보 보내기
    @POST(API.REISSUE) // Replace with your API endpoint
    fun postReissueData(@Body tokenData: TokenData): Call<ReissueData>

    // 이메일 보내기
    @POST(API.EMAIL_VERIFY) // Replace with your API endpoint
    fun postEmailVerifyData(@Body authCode: AuthCodeData): Call<EmailResponse>


    // 카카오 토큰 보내기
    @POST(API.KAKAO_TOKEN) // Replace with your API endpoint
    fun postAccessToken(@Body accessTokenRequest: AccessTokenRequest): Call<TokenResponse>

    // 등록 상품 정보 보내기
    @Multipart
    @POST(API.PRODUCTS) // Replace with your API endpoint
    fun postProducts(
        @Header("Authorization") Authorization: String,
        @Part photos: List<MultipartBody.Part>,
        @Part receipt: MultipartBody.Part,
        @Part ("request") request : Products,
    ): Call<ProductsResponse>

    // 상품 정보 가져오기
    @GET(API.PRODUCTS)
    fun getProducts(
        @Header("Authorization") Authorization: String,
        @Query("userId") userId: Long,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): Call<GetProductsResponse>

    // 도로명 주소 정보 가져오기
    @GET("addrlink/addrLinkApi.do")
    fun getAddress(
        @Query("confmKey") confmKey: String,
        @Query("keyword") keyword: String,
        @Query("resultType") resultType: String
    ): Call<AddressRequest>



}