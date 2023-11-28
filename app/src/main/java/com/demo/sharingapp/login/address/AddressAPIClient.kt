package com.demo.sharingapp.login.address

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AddressAPIClient {
    private const val BASE_URL = "https://business.juso.go.kr/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Authorization","Bearer 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")
                .build()
            it.proceed(request)
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



}