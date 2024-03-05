package com.demo.sharingapp.retrofit

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.sharingapp.data.GetSaveProductData
import com.demo.sharingapp.data.SaveProductsListData
import com.demo.sharingapp.domain.chat.chatroom.data.*
import com.demo.sharingapp.domain.chat.data.GetChatList
import com.demo.sharingapp.domain.chat.data.GetChatListData
import com.demo.sharingapp.domain.home.data.PartProductData
import com.demo.sharingapp.domain.home.data.PartProductListData
import com.demo.sharingapp.domain.home.part.data.DetailedProductData
import com.demo.sharingapp.domain.home.part.data.DetailedProductResponseData
import com.demo.sharingapp.domain.home.search.data.GetSearchData
import com.demo.sharingapp.domain.home.search.data.SearchData
import com.demo.sharingapp.domain.user.data.ChangeMyInfoData
import com.demo.sharingapp.domain.user.data.MyInfoData
import com.demo.sharingapp.domain.user.data.MyInfoResponse
import com.demo.sharingapp.domain.user.data.ChangeMyInfoResponse
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
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.REFRESH_TOKEN
import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import com.demo.sharingapp.utils.Constants.USER_ID
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
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

    // 채팅 리스트 가져오기
    fun getChatList(context: Context, onSuccess: (List<GetChatListData>) -> Unit) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getChatList(Authorization = authorization)

        getCall.enqueue(object : retrofit2.Callback<GetChatList> {
            override fun onResponse(call: Call<GetChatList>, response: Response<GetChatList>) {
                if (response.isSuccessful) {
                    Log.e("getChatList", "success getChatList data ${response.body()?.data}")
                    Log.e("getChatList",
                        "success getChatList success ${response.body()?.success}")
                    Log.e("getChatList",
                        "success getChatList message ${response.body()?.message}")
                    Log.e("getChatList", "success getChatList code ${response.body()?.code}")
                    val data = response.body()?.data ?: return
                    onSuccess(data)
                } else {
                    Log.e("getChatList", "succes, getChatList but ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<GetChatList>, t: Throwable) {
                Log.e("getChatList", "fail ${t} $call")
            }
        })
    }

    // 찜목록 가져오기
    fun getSaveProduct(context: Context, onSuccessData: (SaveProductsListData) -> Unit) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getSaveProducts(Authorization = authorization)

        getCall.enqueue(object : retrofit2.Callback<GetSaveProductData> {
            override fun onResponse(
                call: Call<GetSaveProductData>,
                response: Response<GetSaveProductData>,
            ) {
                if (response.isSuccessful) {
                    Log.e("getSaveProduct", "success getSaveProduct data ${response.body()?.data}")
                    Log.e("getSaveProduct",
                        "success getSaveProduct success ${response.body()?.success}")
                    Log.e("getSaveProduct",
                        "success getSaveProduct message ${response.body()?.message}")
                    Log.e("getSaveProduct", "success getSaveProduct code ${response.body()?.code}")
                    val data = response.body()?.data ?: return
                    onSuccessData(data)

                } else {
                    Log.e("getSaveProduct", "succes, getSaveProduct but ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<GetSaveProductData>, t: Throwable) {
                Log.e("getSaveProduct", "fail ${t} $call")
            }
        })
    }

    // 내정보 - 내 공유글 목록 정보 가져오기
    fun getShareProductList(
        context: Context,
        longitude: Double,
        latitude: Double,
        sort: String?,
        page: Int,
        status: String?,
        onProducts: (PartProductListData) -> Unit,

        ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getShareProductList(
            Authorization = authorization,
            longitude = longitude,
            latitude = latitude,
            page = page,
            size = 30,
            sort = sort,
            status = status,
        )
        getCall.enqueue(object : retrofit2.Callback<PartProductData> {
            override fun onResponse(
                call: Call<PartProductData>,
                response: Response<PartProductData>,
            ) {
                if (response.isSuccessful) {
                    Log.e("getShareProductList", "success ${response.body()}")
                    Log.e("getShareProductList", "success ${response.body()?.data?.last}")
                    val data = response.body()?.data ?: return
                    onProducts(data)

                } else {
                    Log.e("getShareProductList", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<PartProductData>, t: Throwable) {
                Log.e("getShareProductList", "fail ${t} $call")
            }

        })
    }

    // 검색 - 검색 정보 가져오기
    fun getSearch(
        context: Context,
        longitude: Double,
        latitude: Double,
        page: Int,
        q: String,
        onProducts: (SearchData) -> Unit,
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getSearch(
            Authorization = authorization,
            longitude = longitude,
            latitude = latitude,
            page = page,
            sort = "createAt-desc",
            size = 30,
            searchEndDate = null,
            searchStartDate = null,
            q = q
        )
        getCall.enqueue(object : retrofit2.Callback<GetSearchData> {
            override fun onResponse(call: Call<GetSearchData>, response: Response<GetSearchData>) {
                if (response.isSuccessful) {
                    Log.e("getSearch", "success ${response.body()}")
                    val data = response.body()?.data ?: return
                    onProducts(data)
                } else {
                    Log.e("getSearch", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetSearchData>, t: Throwable) {
                Log.e("getSearch", "fail ${t} $call")
            }
        })

    }

    // 카테고리별 상품 가져오기
    fun getPartProduct(
        context: Context,
        longitude: Double,
        latitude: Double,
        oldAccessToken: String,
        categoryId: Long,
        sort: String?,
        page: Int,
        searchStartDate: String?,
        searchEndDate: String?,
        onProducts: (PartProductListData) -> Unit,
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getPartProducts(Authorization = authorization,
            longitude = longitude,
            latitude = latitude,
            categoryId = categoryId,
            page = page,
            size = 30,
            sort = sort,
            searchStartDate = searchStartDate,
            searchEndDate = searchEndDate
        )
        //userId.value?:0
        getCall.enqueue(object : retrofit2.Callback<PartProductData> {
            override fun onResponse(
                call: Call<PartProductData>,
                response: Response<PartProductData>,
            ) {
                if (response.isSuccessful) {
                    Log.e("Get", "success ${response.body()}")
                    val data = response.body()?.data ?: return
                    onProducts(data)

                } else {
                    Log.e("Get", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<PartProductData>, t: Throwable) {
                Log.e("Get", "fail ${t} $call")
            }


        })
    }


    // 홈 상품 가져오기
    fun getProduct(
        context: Context,
        longitude: Double,
        latitude: Double,
        oldAccessToken: String,
        onProducts: (List<DataGetProducts>) -> Unit,
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val getCall = api.getProducts(Authorization = authorization,
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

    // 메이화면 토큰 재발행
    suspend fun postReissueMain(context: Context): Boolean {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val refreshToken = SharedPreferencesData.getData(context, REFRESH_TOKEN)
        val data = TokenData(accessToken, refreshToken)
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postReissueData(data)
        return withContext(Dispatchers.IO) {
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val newAccessToken = response.body()?.data?.accessToken
                    val newRefreshToken = response.body()?.data?.refreshToken
                    if (newAccessToken != null && newRefreshToken != null) {
                        SharedPreferencesData.saveData(context, ACCESS_TOKEN, newAccessToken)
                        SharedPreferencesData.saveData(context, REFRESH_TOKEN, newRefreshToken)
                    }
                    Log.e("Post gn 토큰 ", "$newAccessToken")
                    val success = response.body()?.success ?: false
                    Log.e("Post gn 토큰 ", "$success")
                    success
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("aa", "오류 발생")
                false
            }
        }


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
    fun postFindIdEmailVerify(
        findIdEmailVerifyData: FindIdEmailVerifyData,
        onCheckCode: (Boolean, String, LonginId?) -> Unit,
    ) {
        val call = retrofitInterface?.postFindIdEmailVerify(findIdEmailVerifyData)
        call?.enqueue(object : Callback<FindIdResponse> {
            override fun onResponse(
                call: Call<FindIdResponse>,
                response: Response<FindIdResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("postFindIdEmailVerify", "success Signup data ${response.body()?.data}")
                    Log.e("postFindIdEmailVerify",
                        "success Signup success ${response.body()?.success}")
                    Log.e("postFindIdEmailVerify",
                        "success Signup message ${response.body()?.message}")
                    Log.e("postFindIdEmailVerify", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckCode(response.body()!!.success,
                            response.body()!!.message,
                            response.body()?.data)
                    }
                } else {
                    Log.e("postFindIdEmailVerify", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<FindIdResponse>, t: Throwable) {
                Log.e("postFindIdEmailVerify", "fail ${t} $call")
            }
        })
    }

    // 아이디 찾기 이메일 인증 코드 받기
    fun postFindIdEmail(findIdData: FindIdData, onCheckEmail: (Boolean, String) -> Unit) {
        val call = retrofitInterface?.postFindId(findIdData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postFindIdEmail", "success Signup data ${response.body()?.data}")
                    Log.e("postFindIdEmail", "success Signup success ${response.body()?.success}")
                    Log.e("postFindIdEmail", "success Signup message ${response.body()?.message}")
                    Log.e("postFindIdEmail", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckEmail(response.body()!!.success, response.body()!!.message)
                    }

                } else {
                    Log.e("postFindIdEmail", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindIdEmail", "fail ${t} $call")
            }
        })
    }


    // 비밀번호 찾기 - 이메일 인증코드 받기
    fun postFindPasswordEmail(
        findPasswordData: FindPasswordEmailData,
        onCheckEmail: (Boolean, String) -> Unit,
    ) {
        val call = retrofitInterface?.postFindPasswordEmail(findPasswordData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postFindPasswordEmail", "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordEmail",
                        "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordEmail",
                        "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordEmail", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckEmail(response.body()!!.success, response.body()!!.message)
                    }

                } else {
                    Log.e("postFindPasswordEmail", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordEmail", "fail ${t} $call")
            }
        })
    }

    // 로그아웃
    fun postLogout(
        context: Context
    ){
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postLogout(Authorization = authorization)

        call.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "정상적으로 채팅방을 나갔습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("postLogout", "data ${response.body()?.data}")
                    Log.e("postLogout", "success ${response.body()?.success}")
                    Log.e("postLogout", "message ${response.body()?.message}")
                    Log.e("postLogout", "code ${response.body()?.code}")
                } else {
                    Log.e("postLogout", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postLogout", "fail ${t} $call")
            }
        })
    }

    // 비밀번호 찾기 - 이메일 인증코드 보내기
    fun postFindPasswordEmailVerify(
        findPasswordEmailVerifyData: FindPasswordEmailVerifyData,
        onCheckCode: (Boolean, String) -> Unit,
    ) {
        val call = retrofitInterface?.postFindPasswordEmailVerify(findPasswordEmailVerifyData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postFindPasswordEmailVerify",
                        "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordEmailVerify",
                        "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordEmailVerify",
                        "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordEmailVerify",
                        "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckCode(response.body()!!.success, response.body()!!.message)
                    }

                } else {
                    Log.e("postFindPasswordEmailVerify",
                        "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordEmailVerify", "fail ${t} $call")
            }
        })
    }

    // 내정보 - 닉내임 변경
    fun patchChangeNickname(
        context: Context,
        accessToken: String,
        nickname: String,
        description: String,
        onCheckNickname: (Boolean, String) -> Unit,
    ) {
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val changeMyInfoData = ChangeMyInfoData(nickname, description)
        Log.e("a", description.toString())
        val call = api.patchChangeNickname(Authorization = "Bearer $accessToken", changeMyInfoData)
        call.enqueue(object : retrofit2.Callback<ChangeMyInfoResponse> {
            override fun onResponse(
                call: Call<ChangeMyInfoResponse>,
                response: Response<ChangeMyInfoResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("patchChangeNickname",
                        "success Nickname success ${response.body()?.success}")
                    Log.e("patchChangeNickname",
                        "success Nickname message ${response.body()?.message}")
                    Log.e("patchChangeNickname", "success Nickname code ${response.body()?.code}")
                    val success = response.body()?.success ?: return
                    val message = response.body()?.message ?: return
                    onCheckNickname(success, message)
                } else {
                    Log.e("patchChangeNickname", "succes, Nickname but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<ChangeMyInfoResponse>, t: Throwable) {
                Log.e("patchChangeNickname", "fail ${t} $call")
            }
        })
    }

    // 비밀번호 찾기 - 재변경한 비밀번호 보내기
    fun patchFindPasswordReset(
        findPasswordRestData: FindPasswordResetData,
        onCheckPassword: (Boolean, String) -> Unit,
    ) {
        val call = retrofitInterface?.patchFindPasswordReset(findPasswordRestData)
        call?.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postFindPasswordReset", "success Signup data ${response.body()?.data}")
                    Log.e("postFindPasswordReset",
                        "success Signup success ${response.body()?.success}")
                    Log.e("postFindPasswordReset",
                        "success Signup message ${response.body()?.message}")
                    Log.e("postFindPasswordReset", "success Signup code ${response.body()?.code}")
                    if (response.body()?.success != null && response.body()?.message != null) {
                        onCheckPassword(response.body()!!.success, response.body()!!.message)
                    }
                } else {
                    Log.e("postFindPasswordReset", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postFindPasswordReset", "fail ${t} $call")
            }
        })
    }

    // 회원가입 정보 보내기
    fun postSignup(
        signupData: SignupData,
        profile: MultipartBody.Part,
        signupSuccess: (Boolean) -> Unit,
    ) {
        val call = retrofitInterface?.postSignupData(signUpRequest = signupData, profile = profile)
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

    // 채팅방 개설
    fun postChatRoom(
        context: Context,
        productsId: Long,
        onSuccess: (CreateChatRoomResponseData) -> Unit
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val data = CreateChatRoomData(productsId)
        val call = api.postChatRoom(Authorization = authorization, createChatRoomData = data)
        call.enqueue(object : retrofit2.Callback<CreateChatRoomResponse> {
            override fun onResponse(
                call: Call<CreateChatRoomResponse>,
                response: Response<CreateChatRoomResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("Post", "success createMember ${response.body()?.data?.createMember}")
                    Log.e("Post", "success createMember ${response.body()?.success}")
                    Log.e("Post", "success chatId ${response.body()?.data?.chatId}")
                    Log.e("Post", "success regDate ${response.body()?.data?.regDate}")
                    Log.e("Post", "success joinMember ${response.body()?.data?.joinMember}")
                    Log.e("Post", "success productNo ${response.body()?.data?.productNo}")
                    val data = response.body()?.data?: return
                    onSuccess(data)
                } else {
                    Log.e("Post", "succes, but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<CreateChatRoomResponse>, t: Throwable) {
                Log.e("Post", "fail ${t} $call")
            }
        })
    }

    // 토큰 보내기
    fun postKaKaoToken(
        context: Context,
        token: LoginTokenData,
        onExistsUser: (Boolean) -> Unit,
    ) {

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
                    val existsUser = response.body()?.data?.existsUser ?: return
                    onExistsUser(existsUser)
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
            ACCESS_TOKEN,
            response.body()?.data?.accessToken ?: return)
        SharedPreferencesData.saveData(context,
            REFRESH_TOKEN,
            response.body()?.data?.refreshToken ?: return)
        SharedPreferencesData.saveData(context,
            NICKNAME,
            response.body()?.data?.nickname ?: return)
        SharedPreferencesData.saveLongData(context,
            USER_ID,
            response.body()?.data?.userId ?: return)
    }

    // 상품 좋아요 여부 보내기
    fun postProductLike(context: Context, productsId: Long) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postProductLike(Authorization = authorization, productId = productsId)

        call.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("postProductLike", "success email ${response.body()?.data}")
                    Log.e("postProductLike", "success email ${response.body()?.success}")
                    Log.e("postProductLike", "success email ${response.body()?.message}")
                    Log.e("postProductLike", "success email ${response.body()?.code}")
                } else {
                    Log.e("postProductLike", "succes, Signup but ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("postProductLike", "fail ${t} $call")
            }
        })
    }

    // 상품 삭제 하기
    fun getRemoveProduct(context: Context, productsId: Long) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.getRemoveProduct(Authorization = "Bearer $accessToken", productsId)

        call.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Log.e("getRemoveProduct", "success ${response.body()?.success}")
                    Log.e("getRemoveProduct", "data ${response.body()?.data}")
                    Log.e("getRemoveProduct", "message ${response.body()?.message}")
                    Log.e("getRemoveProduct", "code ${response.body()?.code}")
                    if (response.body()?.success == true) {
                        Toast.makeText(context, response.body()?.data, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("getRemoveProduct", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("getRemoveProduct", "fail ${t} $call")
            }
        })
    }

    // 채팅내역가져오기
    fun getChatRoomData(context: Context, roomNum: Long, onSuccess: (GetChatRoomData) -> Unit) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.getChatRoomData(Authorization = "Bearer $accessToken", roomNum)

        call.enqueue(object : retrofit2.Callback<GetChatRoomResponse> {
            override fun onResponse(call: Call<GetChatRoomResponse>, response: Response<GetChatRoomResponse>) {
                if (response.isSuccessful) {
                    Log.e("getRemoveProduct", "success ${response.body()?.success}")
                    Log.e("getRemoveProduct", "data ${response.body()?.data}")
                    Log.e("getRemoveProduct", "message ${response.body()?.message}")
                    Log.e("getRemoveProduct", "code ${response.body()?.code}")
                    val data = response.body()?.data ?: return
                    onSuccess(data)

                } else {
                    Log.e("getRemoveProduct", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<GetChatRoomResponse>, t: Throwable) {
                Log.e("getRemoveProduct", "fail ${t} $call")
            }
        })
    }

    // 내 정보 조회하기
    fun getMyInfo(context: Context, onSuccess: (MyInfoData) -> Unit) {
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val call = api.getMyInfo(Authorization = "Bearer $accessToken")
        call.enqueue(object : retrofit2.Callback<MyInfoResponse> {
            override fun onResponse(
                call: Call<MyInfoResponse>,
                response: Response<MyInfoResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("getMyInfo", "success ${response.body()?.success}")
                    Log.e("getMyInfo", "data ${response.body()?.data}")
                    Log.e("getMyInfo", "message ${response.body()?.message}")
                    Log.e("getMyInfo", "code ${response.body()?.code}")
                    val data = response.body()?.data ?: return
                    onSuccess(data)

                } else {
                    Log.e("getMyInfo", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<MyInfoResponse>, t: Throwable) {
                Log.e("getMyInfo", "fail ${t} $call")
            }
        })
    }

    // 채팅 메시지 전송 콜백
    fun postChatSendCallBack(
        context: Context,
        chatSendCallBack : ChatSendCallBack,
        onSuccess: (ChatRoomCallBack) -> Unit
    ){
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.postChatSendCallBack(Authorization =  authorization, chatSendCallBack = chatSendCallBack)

        call.enqueue(object : retrofit2.Callback<ChatRoomCallBack>{
            override fun onResponse(call: Call<ChatRoomCallBack>, response: Response<ChatRoomCallBack>) {
                if (response.isSuccessful) {
                    Log.e("getMyInfo", "success ${response.body()?.data?.readCount}")
                    val success = response.body()?.success ?: return
                    if (success){
                        val data = response.body() ?: return
                        onSuccess(data)
                    }
                }else{
                    Log.e("getMyInfo", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<ChatRoomCallBack>, t: Throwable) {
                Log.e("getMyInfo", "fail ${t} $call")
            }
        })
    }

    // 상세 화면 불러오기
    fun getDetailedProduct(
        context: Context,
        productsId: Long,
        accessToken: String,
        onProductData: (DetailedProductData) -> Unit,
    ) {
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.getDetailedProduct(Authorization = "Bearer $accessToken", productsId)

        call.enqueue(object : retrofit2.Callback<DetailedProductResponseData> {
            override fun onResponse(
                call: Call<DetailedProductResponseData>,
                response: Response<DetailedProductResponseData>,
            ) {
                if (response.isSuccessful) {
                    Log.e("getDetailedProduct", "success ${response.body()?.success}")
                    Log.e("getDetailedProduct", "data ${response.body()?.data}")
                    Log.e("getDetailedProduct", "message ${response.body()?.message}")
                    Log.e("getDetailedProduct", "code ${response.body()?.code}")
                    val data = response.body()?.data ?: return
                    onProductData(data)
                } else {
                    Log.e("getDetailedProduct", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<DetailedProductResponseData>, t: Throwable) {
                Log.e("getDetailedProduct", "fail ${t} $call")
            }
        })
    }


    // 회원가입- 카카오
    fun postKaKaoProfile(
        context: Context,
        token: String,
        profile: MultipartBody.Part,
        onSuccess: (Int) -> Unit,
    ) {
        val call = retrofitInterface?.postKaKaoProfile(profile = profile,
            oAuth2KakaoRequest = AccessTokenRequest(token))
        call?.enqueue(object : retrofit2.Callback<TokenResponse> {
            override fun onResponse(
                call: Call<TokenResponse>,
                response: Response<TokenResponse>,
            ) {
                if (response.isSuccessful) {
                    Log.e("Post", "success nickname ${response.body()?.data?.nickname}")
                    Log.e("Post", "success userId ${response.body()?.data?.userId}")
                    Log.e("Post", "success accessToken ${response.body()?.data?.accessToken}")
                    val code = response.body()?.code ?: return
                    onSuccess(code)

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
        val call = api.postProducts(
            Authorization = "Bearer $token",
            request = request,
            photos = photos,
            receipt = receipt,
        )

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

    // 상품 공유 요청
    fun putProductRequest(
        context: Context,
        productsId: Long
    ){
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
//        val retrofit = initRetrofit(context)
//        val api = retrofit.create(RestAPI::class.java)
//        val call = api.putProductRequest(
//            Authorization = authorization,
//            productId = productsId
//        )
        val call = retrofitInterface?.putProductRequest(Authorization = authorization,
            productId = productsId)

        call?.enqueue(object : retrofit2.Callback<EmailResponse>{
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "정상적으로 채팅방을 나갔습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("putProductRequest", "data ${response.body()?.data}")
                    Log.e("putProductRequest", "success ${response.body()?.success}")
                    Log.e("putProductRequest", "message ${response.body()?.message}")
                    Log.e("putProductRequest", "code ${response.body()?.code}")
                } else {
                    Log.e("putProductRequest", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("putProductRequest", "fail ${t} $call")
            }
        })
    }

    // putLeaveChatRoom
    fun putLeaveChatRoom(
        context: Context,
        chatRoomId: Long
    ) {
        val accessToken = SharedPreferencesData.getData(context, ACCESS_TOKEN)
        val authorization = "Bearer $accessToken"
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.putLeaveChatRoom(
            Authorization = authorization,
            chatRoomId = chatRoomId
        )

        call.enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(
                call: Call<EmailResponse>,
                response: Response<EmailResponse>,
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "정상적으로 채팅방을 나갔습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("Post", "success ${response.body()?.data}")
                    Log.e("Post", "success ${response.body()?.success}")
                    Log.e("Post", "success ${response.body()?.message}")
                    Log.e("Post", "success ${response.body()?.code}")
                } else {
                    Log.e("Post", "succes, but ${response.errorBody()}")

                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("Post", "fail ${t} $call")
            }
        })

    }

    // 상품 수정하기
    fun putProductModify(
        context: Context,
        accessToken: String,
        productsId: Long,
        request: Products,
        receipt: MultipartBody.Part,
        photos: List<MultipartBody.Part>,
    ) {
        val token = accessToken
        val retrofit = initRetrofit(context)
        val api = retrofit.create(RestAPI::class.java)
        val call = api.putProductsModify(
            Authorization = "Bearer $token",
            productId = productsId,
            request = request,
            photos = photos,
            receipt = receipt,
        )

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
            .baseUrl(API.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        return retrofit
    }


}