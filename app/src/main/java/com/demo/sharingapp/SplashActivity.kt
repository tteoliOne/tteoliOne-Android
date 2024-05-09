package com.demo.sharingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.login.UserPlace
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable {
            // 앱의 MainActivity로 넘어가기
            checkHasLogin()
        }, 3000) // 3초
    }

    // 로그인 상태 확인
    private fun checkHasLogin() {

        val checkIsRefreshToken = SharedPreferencesData.containsData(this, Constants.REFRESH_TOKEN)
        val checkIsLongitude = SharedPreferencesData.containsData(this, Constants.LONGITUDE)
        val checkIsLatitude = SharedPreferencesData.containsData(this, Constants.LATITUDE)
        Log.e("Log", "1")
        if (checkIsRefreshToken) {
            val reissueData =
                runBlocking { RetrofitManager.instance.postReissueMain(this@SplashActivity) }
            if (!reissueData) {
                Log.e("Log", "2 $reissueData")
                moveLogin()
            }else if(!checkIsLatitude && !checkIsLongitude){
                val i = Intent(this@SplashActivity,UserPlace::class.java)
                startActivity(i)
                // 현재 액티비티 닫기
                finish()
            }else{
                val i = Intent(this@SplashActivity,MainActivity::class.java)
                startActivity(i)
                // 현재 액티비티 닫기
                finish()
            }
        } else {
            Log.e("Log", "3")
            moveLogin()
        }

    }

    // 로그인 화면으로 이동 함수
    private fun moveLogin() {
        val i = Intent(this@SplashActivity,MainActivity::class.java)
        startActivity(i)
        // 현재 액티비티 닫기
        finish()
    }
}