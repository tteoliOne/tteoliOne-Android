package com.demo.sharingapp.login.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.demo.sharingapp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var navHostSignUoFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        navHostSignUoFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_signup_fragment) as NavHostFragment


    }
}