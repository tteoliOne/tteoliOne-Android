package com.demo.sharingapp.login.find_id

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.demo.sharingapp.R

class FindIdActivity : AppCompatActivity() {

    private lateinit var navHostFindIdFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_id)

        navHostFindIdFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_find_id_fragment) as NavHostFragment


    }
}