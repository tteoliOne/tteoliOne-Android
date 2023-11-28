package com.demo.sharingapp.domain

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.sharingapp.login.data.TokenResponse
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.USER_ID
import retrofit2.Call
import retrofit2.Response

class MainViewModel(application: Application): AndroidViewModel(application) {


    private val _nickname = MutableLiveData<String>()
    val nickname : LiveData<String>
        get() = _nickname

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int>
        get() = _userId

    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double>
        get() = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double>
        get() = _longitude


    init {
        // SharedPreferencesData.getIntData(getApplication(), Constants.USER_ID)

    }


    fun updateInt( input: Long){
        SharedPreferencesData.saveLongData(getApplication(), USER_ID, input)
    }

    fun updateMyPlace(longitude: Double, latitude: Double){
        this._longitude.value = longitude
        this._latitude.value = latitude
    }

    fun updateValue(type:String, input: String){
        SharedPreferencesData.saveData(getApplication(), type, input)
//        Log.e("TAG", "MainActivity - myNumberViewModel - currentValue 라이브 데이터 값 변경1 : ${currentUserInput.value}")
//        Log.e("TAG", "MainActivity - myNumberViewModel - currentUserId 라이브 데이터 값 변경1 : ${currentUserId.value}")
    }



}