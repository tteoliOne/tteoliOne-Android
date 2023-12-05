package com.demo.sharingapp.retrofit

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.sharingapp.login.data.*
import com.demo.sharingapp.login.find_id.data.FindIdData
import com.demo.sharingapp.login.find_id.data.FindIdEmailVerifyData
import com.demo.sharingapp.login.find_id.data.FindIdResponse
import com.demo.sharingapp.login.find_id.data.LonginId
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailData
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailVerifyData
import com.demo.sharingapp.login.find_password.data.FindPasswordResetData
import com.demo.sharingapp.login.signup.data.*
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.API
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.REFRESH_TOKEN
import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager() : Application() {

    private val _data = MutableLiveData<List<DataGetProducts>>()
    val data: LiveData<List<DataGetProducts>>
        get() = _data

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String>
        get() = _accessToken

    private val _refreshToken = MutableLiveData<String>()
    val refreshToken: LiveData<String>
        get() = _refreshToken


    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = RetrofitManager()
    }


    // 레트로핏 인터페이스 가져오기
    private val retrofitInterface: RestAPI? =
        RetrofitClient.getClient(API.BASE_URL)?.create(RestAPI::class.java)


    // 상품 가져오기
    fun getProduct(
        context: Context,
        longitude: Double,
        latitude: Double,
        oldAccessToken: String,
        userId: Long,
        //
        onProducts: (List<DataGetProducts>) -> Unit,
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getProducts(Authorization = authorization,
            userId = userId,
            longitude = longitude,
            latitude = latitude)
        //userId.value?:0
        getCall.enqueue(object : retrofit2.Callback<GetProductsResponse> {
            override fun onResponse(
                call: Call<GetProductsResponse>,
                response: Response<GetProductsResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("Get", "success ${response.body()}")
                    val data = response.body()?.data?.list
                    _data.value = data ?: return
                    //
                    onProducts(data)

                } else {
                    if (response.code() == 401) {
                        if (oldAccessToken == accessToken) {
                            //postReissue(context)
                        }
                        // getProduct(context,longitude,latitude,oldAccessToken,userId)
                    }
                    Log.e("Get", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetProductsResponse>, t: Throwable) {
                Log.e("Get", "fail ${t} $call")
            }


        })
    }


    //토큰 재발행
    suspend fun postReissue(context: Context): ReissueData? {

        return withContext(Dispatchers.IO) {
            try {
                Log.e("Post s", "토큰 재발급 시작")

                val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
                val refreshToken = SharedPreferencesData.getData(context, REFRESH_TOKEN)

                Log.e("Post 전 토큰 ", "$accessToken")
                val data = TokenData(accessToken, refreshToken)
                val retrofit = initRetrofit(context)
                val api = retrofit.create(RestAPI::class.java)
                val call = api.postReissueData(data)
                val response = call.execute()
                if (response.isSuccessful) {
                    Log.e("Post s", "토큰 재발급 중")
                    val newAccessToken = response.body()?.data?.accessToken
                    val newRefreshToken = response.body()?.data?.refreshToken
                    if (newAccessToken != null && newRefreshToken != null) {
                        SharedPreferencesData.saveData(context, ACCESS_TOKEN, newAccessToken)
                        SharedPreferencesData.saveData(context, REFRESH_TOKEN, newRefreshToken)
                        Log.e("Post gn 토큰 ", "$newAccessToken")
                    }
                    response.body()
                } else {
                    // 실패한 경우 예외 처리
                    // 예를 들면 throw Exception("Error: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("aa", "오류 발생")
                null
            }
        }


    }


    // 로그인 정보 보내기
    fun postLogin(context: Context, loginData: LoginData, loginCheck: (Boolean) -> Unit) {
        val call = retrofitInterface?.postLoginData(loginData)
        call?.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("PostLogin", "success Login data ${response.body()?.data}")
                    Log.e("PostLogin", "success Login success ${response.body()?.success}")
                    Log.e("PostLogin", "success Login message ${response.body()?.message}")
                    Log.e("PostLogin", "success Login code ${response.body()?.code}")
                    val loginBoolean = response.body()!!.success
                    loginCheck(loginBoolean)
                    saveDate(context, response)
                } else {
                    Log.e("PostLogin", "succes, Login but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("PostLogin", "fail ${t} $call")
            }
        })
    }

    fun postCheckNickname(nicknameData: NicknameData, onCheckNickname: (Boolean, String) -> Unit) {
        val call = retrofitInterface?.postCheckNickname(nicknameData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postCheckNickname", "success Signup data ${response.body()?.data}")
                    Log.e("postCheckNickname", "success Signup success ${response.body()?.success}")
                    Log.e("postCheckNickname", "success Signup message ${response.body()?.message}")
                    Log.e("postCheckNickname", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckNickname(response.body()!!.success, response.body()!!.message)
                    }
                } else {
                    Log.e("postCheckNickname", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postCheckNickname", "fail ${t} $call")
            }
        })
    }

    // 아이디 확인 체크 보내기
    fun postCheckId(idData: IdData, onSuccess: (Boolean, String) -> Unit) {
        val call = retrofitInterface?.postCheckId(idData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postCheckId", "success Signup data ${response.body()?.data}")
                    Log.e("postCheckId", "success Signup success ${response.body()?.success}")
                    Log.e("postCheckId", "success Signup message ${response.body()?.message}")
                    Log.e("postCheckId", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onSuccess(response.body()!!.success, response.body()!!.message)
                    }

                } else {
                    Log.e("postCheckId", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postCheckId", "fail ${t} $call")
            }
        })
    }

    // 아이디 찾기 - 이메일 인증코드 보내기
    fun postFindIdEmailVerify(findIdEmailVerifyData: FindIdEmailVerifyData, onCheckCode: (Boolean, String, LonginId?)-> Unit){
        val call = retrofitInterface?.postFindIdEmailVerify(findIdEmailVerifyData)
        call?.enqueue(object : Callback<FindIdResponse>{
            override fun onResponse(
                call: Call<FindIdResponse>,
                response: Response<FindIdResponse>,
            ) {
                if (response.isSuccessful){
                    Log.e("postFindIdEmailVerify", "success Signup data ${response.body()?.data}")
                    Log.e("postFindIdEmailVerify", "success Signup success ${response.body()?.success}")
                    Log.e("postFindIdEmailVerify", "success Signup message ${response.body()?.message}")
                    Log.e("postFindIdEmailVerify", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null){
                        onCheckCode(response.body()!!.success, response.body()!!.message, response.body()?.data)
                    }
                }else{
                    Log.e("postFindIdEmailVerify", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<FindIdResponse>, t: Throwable) {
                Log.e("postFindIdEmailVerify", "fail ${t} $call")
            }
        })
    }

    // 아이디 찾기 이메일 인증 코드 받기
    fun postFindIdEmail(findIdData: FindIdData, onCheckEmail: (Boolean, String) -> Unit){
        val call = retrofitInterface?.postFindId(findIdData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful){
                    Log.e("postFindIdEmail", "success Signup data ${response.body()?.data}")
                    Log.e("postFindIdEmail", "success Signup success ${response.body()?.success}")
                    Log.e("postFindIdEmail", "success Signup message ${response.body()?.message}")
                    Log.e("postFindIdEmail", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null){
                        onCheckEmail(response.body()!!.success, response.body()!!.message)
                    }

                }else{
                    Log.e("postFindIdEmail", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindIdEmail", "fail ${t} $call")
            }
        })
    }


    // 비밀번호 찾기 - 이메일 인증코드 받기
    fun postFindPasswordEmail(findPasswordData: FindPasswordEmailData, onCheckEmail: (Boolean, String) -> Unit){
        val call = retrofitInterface?.postFindPasswordEmail(findPasswordData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful){
                    Log.e("postFindPasswordEmail", "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordEmail", "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordEmail", "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordEmail", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null){
                        onCheckEmail(response.body()!!.success, response.body()!!.message)
                    }

                }else{
                    Log.e("postFindPasswordEmail", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordEmail", "fail ${t} $call")
            }
        })
    }

    // 비밀번호 찾기 - 이메일 인증코드 보내기
    fun postFindPasswordEmailVerify(findPasswordEmailVerifyData: FindPasswordEmailVerifyData, onCheckCode: (Boolean, String) -> Unit){
        val call = retrofitInterface?.postFindPasswordEmailVerify(findPasswordEmailVerifyData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful){
                    Log.e("postFindPasswordEmailVerify", "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordEmailVerify", "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordEmailVerify", "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordEmailVerify", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null){
                        onCheckCode(response.body()!!.success, response.body()!!.message)
                    }

                }else{
                    Log.e("postFindPasswordEmailVerify", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordEmailVerify", "fail ${t} $call")
            }
        })
    }

    // 비밀번호 찾기 - 재변경한 비밀번호 보내기
    fun postFindPasswordReset(findPasswordRestData: FindPasswordResetData){
        val call = retrofitInterface?.postFindPasswordReset(findPasswordRestData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful){
                    Log.e("postFindPasswordReset", "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordReset", "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordReset", "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordReset", "success Signup code ${response.body()?.code}")

                }else{
                    Log.e("postFindPasswordReset", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordReset", "fail ${t} $call")
            }
        })
    }

    // 회원가입 정보 보내기
    fun postSignup(signupData: SignupData, signupSuccess: (Boolean) -> Unit) {
        val call = retrofitInterface?.postSignupData(signupData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e("PostSignup", "success Signup data ${response.body()?.data}")
                    Log.e("PostSignup", "success Signup success ${response.body()?.success}")
                    Log.e("PostSignup", "success Signup message ${response.body()?.message}")
                    Log.e("PostSignup", "success Signup code ${response.body()?.code}")
                    signupSuccess(response.body()!!.success)

                } else {
                    Log.e("PostSignup", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("PostSignup", "fail ${t} $call")
            }
        })
    }

    // 이메일 인증 보내기
    fun postEmailVerify(authCode: AuthCodeData, nextScreen: (Boolean) -> Unit) {
        val call = retrofitInterface?.postEmailVerifyData(authCode = authCode)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("PostEmail", "success email ${response.body()?.data}")
                    Log.e("PostEmail", "success email ${response.body()?.success}")
                    Log.e("PostEmail", "success email ${response.body()?.message}")
                    Log.e("PostEmail", "success email ${response.body()?.code}")
                    if (response.body() != null) {
                        Log.e("PostEmail", response!!.body()!!.success.toString())
                        nextScreen(response!!.body()!!.success)
                    }
                } else {
                    Log.e("PostEmail", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("PostEmail", "fail ${t} $call")
            }
        })
    }

    // 이메일 보내기
    fun postEmail(context: Context, email: EmailData, onCheckEmail: (Boolean, String) -> Unit) {
        val call = retrofitInterface?.postEmailData(email = email)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    SharedPreferencesData.saveData(context, SIGNUP_EMAIL, email.email)
                    Log.e("PostEmail", "success email ${response.body()?.data}")
                    Log.e("PostEmail", "success email ${response.body()?.success}")
                    Log.e("PostEmail", "success email ${response.body()?.message}")
                    Log.e("PostEmail", "success email ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckEmail(response.body()!!.success, response.body()!!.message)
                    }
                } else {
                    Log.e("PostEmail", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("PostEmail", "fail ${t} $call")
            }
        })
    }

    // 토큰 보내기
    fun postToken(context: Context, token: AccessTokenRequest) {

        val call = retrofitInterface?.postAccessToken(accessTokenRequest = token)
        call?.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onResponse(
                call: Call<TokenResponse>,
                response: Response<TokenResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("Post", "success nickname ${response.body()?.data?.nickname}")
                    Log.e("Post", "success userId ${response.body()?.data?.userId}")
                    Log.e("Post", "success accessToken ${response.body()?.data?.accessToken}")
//                    _nickname.value = response.body()?.data?.nickname
//                    _refreshToken.value = response.body()?.data?.refreshToken
//                    _accessToken.value = response.body()?.data?.accessToken
//                    _userId.value = response.body()?.data?.userId

                    // 사용자 정보 저장 함수 호출
                    saveDate(context, response)

                } else {
                    Log.e("Post", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("Post", "fail ${t} $call")
            }
        })

    }

    // 사용자 정보 저장 함수
    private fun saveDate(
        context: Context,
        response: Response<TokenResponse>,
    ) {

        _accessToken.value = response.body()?.data?.accessToken ?: return
        _refreshToken.value = response.body()?.data?.refreshToken ?: return
        Log.e("kakaoAcRf", "${accessToken.value}, ${refreshToken.value}")
        // request 로 온 데이터 내부에 저장하기
        SharedPreferencesData.saveData(context,
            "accessToken",
            response.body()?.data?.accessToken ?: return)
        SharedPreferencesData.saveData(context,
            "refreshToken",
            response.body()?.data?.refreshToken ?: return)
        SharedPreferencesData.saveData(context,
            "nickname",
            response.body()?.data?.nickname ?: return)
        SharedPreferencesData.saveLongData(context,
            "userId",
            response.body()?.data?.userId ?: return)
    }

    // 상품 좋아요 여부 보내기
    fun postProductLike(context: Context, productsId: Long, accessToken: String) {
        val token = accessToken
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postProductLike(Authorization = "Bearer $token", productId = productsId)

        call.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful){
                    Log.e("postProductLike", "success email ${response.body()?.data}")
                    Log.e("postProductLike", "success email ${response.body()?.success}")
                    Log.e("postProductLike", "success email ${response.body()?.message}")
                    Log.e("postProductLike", "success email ${response.body()?.code}")
                }else{
                    Log.e("postProductLike", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postProductLike", "fail ${t} $call")
            }
        })
    }


    // 상품 등록하기
    fun postProduct(
        context: Context,
        accessToken: String,
        request: Products,
        receipt: MultipartBody.Part,
        photos: List<MultipartBody.Part>,
    ) {
        val token = accessToken
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postProducts(Authorization = "Bearer $token",
            request = request,
            receipt = receipt,
            photos = photos)

        call.enqueue(object : retrofit2.Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>,
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "상품이 등록 되었습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("Post", "success ${response.body()?.userId}")
                    Log.e("Post", "success ${response.body()?.productId}")
                } else {
                    Log.e("Post", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                Log.e("Post", "fail ${t} $call")
            }
        })

    }

    private fun initRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.e("Post", "log: message ${message}")
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)
        client.authenticator(TokenAuthenticator(context))


        val retrofit = Retrofit.Builder()
            .baseUrl("https://tteolione.store")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        return retrofit
    }


}