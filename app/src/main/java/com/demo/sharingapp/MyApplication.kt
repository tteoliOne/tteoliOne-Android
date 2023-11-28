package com.demo.sharingapp

import android.app.Application
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.shared.SharedPreferencesData

class MyApplication: Application() {
    companion object{
        lateinit var mainViewModel : MainViewModel
    }

    override fun onCreate() {
        super.onCreate()
        mainViewModel = MainViewModel(this)
    }
}