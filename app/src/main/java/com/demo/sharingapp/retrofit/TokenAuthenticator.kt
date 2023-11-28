package com.demo.sharingapp.retrofit

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.demo.sharingapp.login.data.TokenData
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (isTokenExpired(response)) {
            Log.e("Authenticator", response.toString())
            Log.e("Authenticator", "토큰 재발급 시도")
            val reissueData = runBlocking {RetrofitManager.instance.postReissue(context)}
            val newAccessToken = reissueData?.data?.accessToken
            Log.e("Authorization Token", newAccessToken.toString())
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()

        }
        return null


//        val newToken = runBlocking {
//            if (isTokenExpired(response)) {
//                var obtainedToken: String? = null
//                RetrofitManager.instance.postReissue(){
//                    obtainedToken = it
//                }
//                obtainedToken ?: ""
//            } else {
//                // 토큰이 만료되지 않은 경우 기존 토큰 반환
//                ""
//            }
//        }
//            if (newToken.isNotEmpty()) {
//
//                Log.e("newToken",newToken)
//                // 새로운 토큰이 성공적으로 얻어졌을 경우, 요청을 재시도
//                return response.request.newBuilder()
//                    .header("Authorization", "Bearer $newToken")
//                    .build()
//            }
//
//        return null
    }

//    private fun request(response: Response): Request {
//        Log.i("Authenticator", "토큰 재발급 성공 : $newAccessToken")
//        return response.request.newBuilder()
//            .removeHeader("Authorization").apply {
//                addHeader("Authorization", "Bearer $newAccessToken")
//            }.build() // 토큰 재발급이 성공했다면, 기존 헤더를 지우고, 새로운 해더를 단다.
//    }


    private fun isTokenExpired(response: Response): Boolean {
        return response.code == 401
    }

}