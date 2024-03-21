package com.demo.sharingapp.shared

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPreferencesData {


    private const val PREFERENCE_NAME = "encrypted_preferences"

    // EncryptedSharedPreferences 초기화
    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFERENCE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // MutableList 데이터 저장
    fun saveMutableListData(context: Context, setTitle: String, setData: MutableList<String>){
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(setTitle, setData.joinToString(separator = ","))

            commit()
        }
    }

    // MutableList 데어터 불러오기
    fun getMutableListData(context: Context, getTitle: String): MutableList<String>? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(getTitle, "")?.split(",")?.toMutableList()
    }



    // 데이터 저장하기
    fun saveData(context: Context, sendTitle: String ,sendValue: String) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(sendTitle, sendValue)

            commit()
        }
    }

    // 데이터가 있는지 확인하기
    fun containsData(context: Context, title: String): Boolean{
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.contains(title)
    }

    // 전체 데이터 제거하기
    fun removeAllData(context: Context){
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()){
            clear()
            apply()
        }
    }



    // 데이터 제거하기
    fun removeData(context: Context, title: String){
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()){
            remove(title)
            commit()
        }
    }

    // 데이터 저장하기
    fun saveIntData(context: Context, sendTitle: String ,sendValue: Int) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putInt(sendTitle, sendValue)
            commit()
        }
    }

    // 데이터 저장하기
    fun saveLongData(context: Context, sendTitle: String ,sendValue: Long) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putLong(sendTitle, sendValue)
            commit()
        }
    }



    // 데이터 가져오기
    fun getData(context: Context, getTitle: String): String {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(getTitle, "") ?: ""
    }

    // 데이터 가져오기
    fun getIntData(context: Context, getTitle: String): Int {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getInt(getTitle, 0) ?: 0
    }

    fun getLongData(context: Context, getTitle: String): Long {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getLong(getTitle, 0) ?: 0
    }




}