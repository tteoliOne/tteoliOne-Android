package com.demo.sharingapp.login.signup

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySignupProfileBinding
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.utils.Constants.KAKAO_TOKEN
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SignupProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupProfileBinding


    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kaKaoToken = intent.getStringExtra(KAKAO_TOKEN) ?: ""
        val packageName = this.packageName
        val resourceId = R.drawable.profile_image_basics
        imageUri = Uri.parse("android.resource://$packageName/$resourceId")

        Glide.with(binding.profileImageView)
            .load(R.drawable.profile_image_basics)
            .circleCrop()
            .into(binding.profileImageView)

        binding.profileChangeButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            activityResult.launch(intent)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
            finish()
        }

        binding.finishButton.setOnClickListener {
            val imageFile = imageToFile(imageUri)
            RetrofitManager.instance.postKaKaoProfile(this, token = kaKaoToken,imageFile){
                if (it == 0){
                    val intent = Intent(this, SignupFinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }else if (it == 1001){
                    Toast.makeText(this, "error code is 1001",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "error",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun imageToFile(uri: Uri): MultipartBody.Part{
            val exifInterface = getExifInterface(this, uri)
            val orientation = exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val rotatedBitmap = rotateBitmap(convertUriToJpeg(uri), getRotationAngle(orientation ?: ExifInterface.ORIENTATION_NORMAL))

            val file = File(this.cacheDir, "profile.jpeg")
            val fileOutputStream = FileOutputStream(file)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("profile", file.name, requestFile)
        return imagePart
    }

    // 휴대폰 설정에 따라 이미지 각도를 돌려줌
    private fun getRotationAngle(orientation: Int): Float {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }

    // uri를 비트맵으로 바꾸는 함수
    private fun convertUriToJpeg(uri: Uri): Bitmap {
        val input = this.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return bitmap
    }

    // 비트맵을 주어진 각도로 회전하여 반환하는 함수
    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // 이미지 uri에서 ExifInterface 가져오기
    fun getExifInterface(context: Context, uri: Uri): ExifInterface? {
        val inputStream = context.contentResolver.openInputStream(uri)
        return if (inputStream != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface(inputStream)
            } else {
                // Android N 이하에서는 파일 경로를 얻어서 ExifInterface 생성
                val realPath = getRealPathFromUri(context, uri)
                realPath?.let {
                    ExifInterface(it)
                }
            }
        } else {
            null
        }
    }

    // uri 에서 절대경로 가져오기
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // Android 10 이상에서는 ContentResolver를 통해 파일 경로 가져오기
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = cursor.getString(columnIndex)
            }
        } else {
            // Android 9 이하에서는 MediaStore를 통해 파일 경로 가져오기
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    realPath = it.getString(columnIndex)
                }
            }
            cursor?.close()
        }

        return realPath
    }

    // 갤러리
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {

        if (it.resultCode == RESULT_OK) {

             // 이미지 한장 골랐을 때
                 imageUri = it.data!!.data ?: imageUri
            Glide.with(binding.profileImageView)
                .load(imageUri)
                .circleCrop()
                .into(binding.profileImageView)
        }
    }

}