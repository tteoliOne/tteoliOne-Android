package com.demo.sharingapp.retrofit

import android.util.Log
import com.demo.sharingapp.login.data.TokenData
import com.demo.sharingapp.utils.API.BASE_URL
import com.kakao.sdk.network.ApiFactory.loggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RefreshTokenService {
    val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
        override fun log(message: String) {
            Log.e("Post","log: message ${message}")
        }
    })


    private val refreshRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val refreshService = refreshRetrofit.create(RestAPI::class.java)


}