package com.demo.sharingapp.retrofit

import com.demo.sharingapp.data.GetSaveProductData
import com.demo.sharingapp.domain.home.data.PartProductData
import com.demo.sharingapp.domain.home.part.data.DetailedProductResponseData
import com.demo.sharingapp.login.data.*
import com.demo.sharingapp.login.find_id.data.FindIdData
import com.demo.sharingapp.login.find_id.data.FindIdEmailVerifyData
import com.demo.sharingapp.login.find_id.data.FindIdResponse
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailData
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailVerifyData
import com.demo.sharingapp.login.find_password.data.FindPasswordResetData
import com.demo.sharingapp.login.signup.data.*
import com.demo.sharingapp.utils.API
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.time.LocalDate

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

    // 상품 좋아요 여부 보내기
    @POST(API.PRODUCT_LIKE) // Replace with your API endpoint
    fun postProductLike(
        @Header("Authorization") Authorization: String,
        @Path ("productId") productId: Long
    ): Call<EmailResponse>

    // 닉네임 중복 확인 보내기
    @POST(API.CHECK_NICKNAME) // Replace with your API endpoint
    fun postCheckNickname(@Body nicknameData: NicknameData): Call<EmailResponse>

    // 로그인 정보 보내기
    @POST(API.LOGIN) // Replace with your API endpoint
    fun postLoginData(@Body loginData: LoginData): Call<TokenResponse>

    // 토큰 재발행 정보 보내기
    @POST(API.REISSUE) // Replace with your API endpoint
    fun postReissueData(@Body tokenData: TokenData): Call<ReissueData>

    // 이메일 인증 코드 보내기
    @POST(API.EMAIL_VERIFY) // Replace with your API endpoint
    fun postEmailVerifyData(@Body authCode: AuthCodeData): Call<EmailResponse>

    // 아이디 찾기 이메일 인증 코드 받기
    @POST(API.FIND_ID) // Replace with your API endpoint
    fun postFindId(@Body findId: FindIdData): Call<EmailResponse>

    // 아이디 찾기 이메일 인증 코드 보내기
    @POST(API.FIND_ID_EMAIL_VERIFY) // Replace with your API endpoint
    fun postFindIdEmailVerify(@Body findIdEmailVerify: FindIdEmailVerifyData): Call<FindIdResponse>

    // 비밀번호 찾기 - 이메일 인증 코드 받기
    @POST(API.FIND_PASSWORD_EMAIL) // Replace with your API endpoint
    fun postFindPasswordEmail(@Body findPassword: FindPasswordEmailData): Call<EmailResponse>

    // 비밀번호 찾기 - 이메일 인증 코드 보내기
    @POST(API.FIND_PASSWORD_EMAIL_VERIFY) // Replace with your API endpoint
    fun postFindPasswordEmailVerify(@Body findPasswordEmailVerify: FindPasswordEmailVerifyData): Call<EmailResponse>

    // 비밀번호 찾기 - 바꾼 비밀번호 보내기
    @PATCH(API.FIND_PASSWORD_RESET) // Replace with your API endpoint
    fun postFindPasswordReset(@Body findPasswordEmailVerify: FindPasswordResetData): Call<EmailResponse>


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

    // 카테고리별 상품 정보 가져오기
    @GET(API.PRODUCTS)
    fun getPartProducts(
        @Header("Authorization") Authorization: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("categoryId") categoryId: Long,
        @Query("searchStartDate") searchStartDate: String?,
        @Query("searchEndDate") searchEndDate: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
        ): Call<PartProductData>

    // 상품 정보 가져오기
    @GET(API.PRODUCTS_SIMPLE)
    fun getProducts(
        @Header("Authorization") Authorization: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): Call<GetProductsResponse>

    // 찜목록 가져오기
    @GET(API.SAVE_PRODUCTS)
    fun getSaveProducts(
        @Header("Authorization") Authorization: String,
    ): Call<GetSaveProductData>

    // 상세 상품 데이터 가져오기
    @GET(API.DETAILED_PRODUCT)
    fun getDetailedProduct(
        @Header("Authorization") Authorization: String,
        @Path ("productId") productId: Long
    ): Call<DetailedProductResponseData>

    // 상품 삭제
    @DELETE(API.DETAILED_PRODUCT)
    fun getRemoveProduct(
        @Header("Authorization") Authorization: String,
        @Path ("productId") productId: Long
    ): Call<EmailResponse>


    // 도로명 주소 정보 가져오기
    @GET("addrlink/addrLinkApi.do")
    fun getAddress(
        @Query("confmKey") confmKey: String,
        @Query("keyword") keyword: String,
        @Query("resultType") resultType: String
    ): Call<AddressRequest>



}