package com.demo.sharingapp.domain.product

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.demo.sharingapp.PermissionUtil
import com.demo.sharingapp.R
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.FIND_LATITUDE
import com.demo.sharingapp.utils.Constants.FIND_LONGITUDE
import com.demo.sharingapp.utils.Constants.FLAG_REQ_CAMERA
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProductBottomSheet( private val productType: Int) : BottomSheetDialogFragment() {
    private lateinit var productViewModel: ProductViewModel

    // 카메라 권한
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)

    private var realUri :Uri? =null

    var isChangeImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.add_products_bottom_sheet, container, false)
    }

    // 테마 설정
    override fun getTheme(): Int {
        return R.style.BottomSheetStyleDialogTheme
    }

    // 액션 설정
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        productViewModel = ViewModelProvider(this.requireActivity())[ProductViewModel::class.java]

        val commitBtn = view?.findViewById<Button>(R.id.applyButton)
        val iv_pre = view?.findViewById<ImageView>(R.id.receiptImageView) ?: return

        // 영수증 이미지 추가 버튼 클릭 시
        view?.findViewById<ConstraintLayout>(R.id.receiptImageButton)?.setOnClickListener {
            // 카메라 함수 호출
            openCamera()
        }

        // 완료 버튼 클릭 시
        commitBtn?.setOnClickListener {
            if (productType == 1){ // 상품 수정하기
                productViewModel.putProductModify()
                val resultIntent = Intent()
                this@ProductBottomSheet.requireActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent)
                this@ProductBottomSheet.requireActivity().finish()
                removeFindPlace()
                dismiss()
            }else{ // 상품 추가하기
                if(isChangeImage) {
                    // 뷰 모델로 데이터 전달 함수 호출
                    productViewModel.postProduct()
                    this@ProductBottomSheet.requireActivity().finish()
                    removeFindPlace()
                    dismiss()
                }else{
                    view?.findViewById<TextView>(R.id.errorMessageTextView)?.isVisible=true
                }
            }




        }

    }

    private fun removeFindPlace() {
        SharedPreferencesData.removeData(this.requireContext(), FIND_LATITUDE)
        SharedPreferencesData.removeData(this.requireContext(), FIND_LONGITUDE)
    }

    // 카메라 함수
    private fun openCamera() {

        // 카메라 권한 있는지 확인
        if (PermissionUtil.checkPermission(this.requireContext(),CAMERA_PERMISSION)){
            // 카메라 권한 있을때
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            createImageUri(generateFileName(), "image/jpg")?.let { uri ->
                realUri = uri
                // MediaStore.EXTRA_OUTPUT을 Key로 하여 Uri를 넘겨주면
                // 일반적인 Camera App은 이를 받아 내가 지정한 경로에 사진을 찍어서 저장시킨다.
                intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            }
            startActivityForResult(intent, Constants.FLAG_REQ_CAMERA)
        }else{
            // 없을때
            // 카메라 권한 요철
            PermissionUtil.requestPermission(this.requireActivity(),CAMERA_PERMISSION)
        }
    }

    // 이미지를 uri로 저장
    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return this.requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    // 카메라로 찍은 이미지 가져오는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{

                    val iv_pre = view?.findViewById<ImageView>(R.id.receiptImageView)
                    realUri?.let { uri ->
                        iv_pre?.setImageURI(uri)
                        isChangeImage = true
                        //
                        val exifInterface = getExifInterface(this.requireContext(), uri)
                        val orientation = exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                        val rotatedBitmap = rotateBitmap(convertUriToJpeg(uri), getRotationAngle(orientation ?: ExifInterface.ORIENTATION_NORMAL))

                        val file = File(requireContext().cacheDir, "receipt.jpeg")
                        val fileOutputStream = FileOutputStream(file)
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
                        fileOutputStream.flush()
                        fileOutputStream.close()

                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        val imagePart = MultipartBody.Part.createFormData("receipt", file.name, requestFile)
                        productViewModel.updateReceipt(imagePart)
                    }
                }
            }
        }
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

    // 비트맵을 주어진 각도로 회전하여 반환하는 함수
    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // uri를 비트맵으로 바꾸는 함수
    private fun convertUriToJpeg(uri: Uri): Bitmap {
        val input = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return bitmap
    }


    // 이미지 이름을 생성 함수
    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "JPEG_$timeStamp.jpeg"
    }


}