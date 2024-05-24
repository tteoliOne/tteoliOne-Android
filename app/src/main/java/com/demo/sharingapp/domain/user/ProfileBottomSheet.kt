package com.demo.sharingapp.domain.user

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.demo.sharingapp.PermissionUtil
import com.demo.sharingapp.R
import com.demo.sharingapp.domain.home.product.ProductViewModel
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileBottomSheet(private val moveGallery:()->Unit, private val clickBasic: () -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.setting_profile_bottom_sheet, container, false)
    }

    // 테마 설정
    override fun getTheme(): Int {
        return R.style.BottomSheetStyleDialogTheme
    }

    // 액션 설정
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val galleryBtn = view?.findViewById<TextView>(R.id.galleryButton)
        val basicBtn = view?.findViewById<TextView>(R.id.basicButton)
        val cancelBtn = view?.findViewById<Button>(R.id.cancelButton)


        galleryBtn?.setOnClickListener {
            moveGallery()
            dismiss()
        }

        basicBtn?.setOnClickListener {
            clickBasic()
            dismiss()
        }

        // 취소 버튼 클릭 시
        cancelBtn?.setOnClickListener {
            dismiss()
        }

    }






}