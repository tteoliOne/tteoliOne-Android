package com.demo.sharingapp.login.find_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.demo.sharingapp.R

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var navHostFindPasswordFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        navHostFindPasswordFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_find_password_fragment) as NavHostFragment
    }
}