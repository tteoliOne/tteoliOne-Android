package com.demo.sharingapp.login.address

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AddressService {
    @GET("addrlink/addrLinkApi.do")
    fun listAddress(
        @Query("confmKey") confmKey: String,
        @Query("keyword") keyword: String,
        @Query("resultType") resultType: String,
    ): Call<UserDto>

    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<UserDto>
}