package com.demo.sharingapp.domain.product

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.demo.sharingapp.PermissionUtil
import com.demo.sharingapp.R
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

class ProductBottomSheet : BottomSheetDialogFragment() {
    private lateinit var productViewModel: ProductViewModel

    // 카메라 권한
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)

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
        Log.e("aa",productViewModel.currentBuyPrice.value.toString())

        //
        val iv_pre = view?.findViewById<ImageView>(R.id.receiptImageView)

        val commitBtn = view?.findViewById<Button>(R.id.applyButton)



        // 영수증 이미지 추가 버튼 클릭 시
        view?.findViewById<ConstraintLayout>(R.id.receiptImageButton)?.setOnClickListener {

            // 카메라 함수 호출
            openCamera()
        }

        // 완료 버튼 클릭 시
        commitBtn?.setOnClickListener {

            if(isChangeImage) {
                // 뷰 모델로 데이터 전달 함수 호출
                productViewModel.postProduct()
                this@ProductBottomSheet.requireActivity().finish()
                dismiss()
            }else{
                view?.findViewById<TextView>(R.id.errorMessageTextView)?.isVisible=true
            }
        }

    }

    // 카메라 함수
    private fun openCamera() {

        // 카메라 권한 있는지 확인
        if (PermissionUtil.checkPermission(this.requireContext(),CAMERA_PERMISSION)){
            // 카메라 권한 있을때
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,FLAG_REQ_CAMERA)
        }else{
            // 없을때
            // 카메라 권한 요철
            PermissionUtil.requestPermission(this.requireActivity(),CAMERA_PERMISSION)
        }
    }

    // 카메라로 찍은 이미지 가져오는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    if(data?.extras?.get("data") != null){
                        //카메라로 방금 촬영한 이미지를 미리 만들어 놓은 이미지뷰로 전달 합니다.
                        val bitmap = data?.extras?.get("data") as Bitmap

                        val iv_pre = view?.findViewById<ImageView>(R.id.receiptImageView)
                        iv_pre?.setImageBitmap(bitmap)
                        isChangeImage = true

                        val uri = saveBitmapToGallery(this.requireContext(),bitmap)
                        if (uri != null){
                            val file = File(requireContext().cacheDir, "image.jpeg")
                            val fileOutputStream = FileOutputStream(file)
                            convertUriToJpeg(uri).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                            fileOutputStream.flush()
                            fileOutputStream.close()

                            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                            val imagePart = MultipartBody.Part.createFormData("receipt", "receipt", requestFile)
                            productViewModel.updateReceipt(imagePart)
                        }
                    }
                }
            }
        }
    }

    // uri를 비트맵으로 바꾸는 함수
    private fun convertUriToJpeg(uri: Uri): Bitmap {
        val input = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return bitmap
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            try {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                    return imageUri
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return imageUri
    }

    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "JPEG_$timeStamp.jpeg"
    }


}