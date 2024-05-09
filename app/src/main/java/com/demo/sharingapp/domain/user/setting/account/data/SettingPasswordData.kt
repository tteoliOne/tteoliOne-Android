package com.demo.sharingapp.domain.user.setting.account.data

import com.google.gson.annotations.SerializedName

data class SettingPasswordData(
    @SerializedName("password")
    val password: String,
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("newPasswordConfirm")
    val newPasswordConfirm: String,
)
