package com.demo.sharingapp.utils

import android.content.Context
import androidx.lifecycle.viewmodel.CreationExtras
import com.demo.sharingapp.login.data.TokenResponse
import com.google.gson.Gson
import java.io.IOException

fun Context.readData(): TokenResponse? {

    return try {
        val inputStream = this.resources.assets.open("ResponseSample.json")
        val buffer = ByteArray(inputStream.available())

        inputStream.read(buffer)
        inputStream.close()

        val gson = Gson()
        gson.fromJson(String(buffer), TokenResponse::class.java)
    } catch (e: IOException){
        null
    }


}

