package com.demo.sharingapp.retrofit

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//싱글턴 = 메모리 하나만 사용
object RetrofitClient {

    // 레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? =null

    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit?{
        Log.d("retrofit" , "RetrofitClient - getClient() called")


        // 로깅 인터셉터
        val client = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.e("Post","log: message ${message}")
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)
        //client.authenticator(TokenAuthenticator())

        if (retrofitClient == null){

            // 레트로핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitClient
    }

}