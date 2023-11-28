package com.demo.sharingapp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        permissionArray.forEach {

            // 권한이 있는지 없는지 확인 checkSelfPermission으로 기존에 권한이 받았는지 권한이 없는지 확인
            if (ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    // 권한 요청
    fun requestPermission(activity: Activity, permissionArray: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissionArray, 1)
    }
}