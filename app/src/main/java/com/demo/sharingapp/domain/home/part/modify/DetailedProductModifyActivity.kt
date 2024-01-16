package com.demo.sharingapp.domain.home.part.modify

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.PermissionUtil
import com.demo.sharingapp.R
import com.demo.sharingapp.addproduct.AddProductImageData
import com.demo.sharingapp.addproduct.ProductImageAdapter
import com.demo.sharingapp.databinding.ActivityDetailedProductModifyBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_COUNT
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_DAY
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_MONTH
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_PRICE
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_YEAR
import com.demo.sharingapp.utils.Constants.PRODUCT_DESCRIPTION
import com.demo.sharingapp.utils.Constants.PRODUCT_IMAGE
import com.demo.sharingapp.utils.Constants.PRODUCT_LATITUDE
import com.demo.sharingapp.utils.Constants.PRODUCT_LONGITUDE
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_COUNT
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_PRICE

import com.demo.sharingapp.utils.Constants.PRODUCT_TITLE
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailedProductModifyActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var binding: ActivityDetailedProductModifyBinding

    // 이미지
    private lateinit var imageAdepter: DetailedProductModifyAdepter
    private var imageList: ArrayList<String> = ArrayList()
    private var realUri: Uri? = null

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageCount(0)

        val productTitle = intent.getStringExtra(PRODUCT_TITLE)
        val productBuyPrice = intent.getStringExtra(PRODUCT_BUY_PRICE)
        val productBuyCount = intent.getStringExtra(PRODUCT_BUY_COUNT)
        val productSharePrice = intent.getStringExtra(PRODUCT_SHARE_PRICE)
        val productShareCount = intent.getStringExtra(PRODUCT_SHARE_COUNT)
        val productBuyYear = intent.getIntExtra(PRODUCT_BUY_YEAR,0)
        val productBuyMonth = intent.getIntExtra(PRODUCT_BUY_MONTH,0)
        val productBuyDay = intent.getIntExtra(PRODUCT_BUY_DAY,0)
        val productDescription = intent.getStringExtra(PRODUCT_DESCRIPTION)
        val productImage = intent.getStringArrayExtra(PRODUCT_IMAGE) ?: return
        productImage.forEach {
            imageList.add(it)
        }
        latitude = intent.getDoubleExtra(PRODUCT_LATITUDE,0.0)
        longitude = intent.getDoubleExtra(PRODUCT_LONGITUDE,0.0)

        settingMap()

        binding.titleEditText.setText(productTitle)
        binding.purchasePriceEditText.setText(productBuyPrice)
        binding.purchasingVolumeEditText.setText(productBuyCount)
        binding.sharePriceEditText.setText(productSharePrice)
        binding.shareVolumeEditText.setText(productShareCount)
        binding.buyDateTextView.text = "$productBuyYear.$productBuyMonth.$productBuyDay"
        binding.descriptionEditText.setText(productDescription)





        // 리사이클러뷰 안에 뷰 클릭 시 뷰 삭제
        imageAdepter = DetailedProductModifyAdepter { postion ->
            imageList.removeAt(postion)
            //이미지 어댑터에서 업데이트 함수 호출
            updateImageList()
        }

        // 리사이클러뷰 어댑터와 레이아웃메니져 설정
        binding.productImageRecyclerView.apply {
            adapter = imageAdepter
            layoutManager = LinearLayoutManager(this@DetailedProductModifyActivity).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
            updateImageList()
        }

        // 이전 버튼 클릭
        binding.backButton.setOnClickListener {
            finish()
        }

        // 상품이미지 버튼 클릭
        binding.addProductImageButton.setOnClickListener {

            // 상품이미지 추가 클릭시 나오는 메뉴 함수 호출
            addProductButtonMenu(it, R.menu.menu_add_product)
        }


    }

    // 상품이미지 추가 클릭시 나오는 메뉴 함수
    private fun addProductButtonMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->

            when (menuItem.itemId) {

                // 카메라 버튼 눌렀을 때
                R.id.option_camera -> {

                    // 카메라로 이동 함수 호출
                    openCamera()
                    return@setOnMenuItemClickListener true
                }

                // 이미지 선택 눌렀을 때
                R.id.option_gallery -> {

                    // 겔러리로 이동 함수 호출
                    moveGallery()
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
        popup.show()
    }

    // 겔러리 이동 함수
    private fun moveGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        // 이미지 여러장 저장
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activityResult.launch(intent)
    }

    // 갤러리
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {

        // 이미지 최대 개수
        val allCount = 5

        if (it.resultCode == RESULT_OK) {

            // 이미지 여러장 골랐을 때
            if (it.data!!.clipData != null) {

                // 현제 선택한 이미지 수
                val count = it.data!!.clipData!!.itemCount

                // 고른 이미지 수
                val totalCount = allCount - imageList.count() - count

                //최대 이미지 수 넘었을 때 제한
                if (totalCount < 0) {
                    Log.e("a", "초과")
                    Toast.makeText(this, "이미지 수가 초과하였습니다.", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }


                // 여러장 이미지 저장
                for (index in 0 until count) {
                    val imageUrl = it.data!!.clipData!!.getItemAt(index).uri.toString()
                    imageList.add(imageUrl)
                }

            } else { // 이미지 한장 골랐을 때
                val imageUri = it.data!!.data.toString()
                imageList.add(imageUri!!)
            }

            //이미지 어댑터에서 업데이트 함수 호출
            updateImageList()


        }
    }

    // 카메라 이동하는 함수
    private fun openCamera() {
        val cameraPermission = arrayOf(Manifest.permission.CAMERA)
        if (PermissionUtil.checkPermission(this, cameraPermission)) {

            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            createImageUri(generateFileName(), "image/jpg")?.let { uri ->
                realUri = uri
                // MediaStore.EXTRA_OUTPUT을 Key로 하여 Uri를 넘겨주면
                // 일반적인 Camera App은 이를 받아 내가 지정한 경로에 사진을 찍어서 저장시킨다.
                intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            }
            startActivityForResult(intent, Constants.FLAG_REQ_CAMERA)
        } else {
            PermissionUtil.requestPermission(this, cameraPermission)
        }

    }
    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    // 갤러리에 저장할때 이미지 이름 정하는 함수
    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "JPEG_$timeStamp.jpeg"
    }

    //이미지 어댑터에서 업데이트 함수
    private fun updateImageList() {
        // 선택한 이미지 개수 업데이트
        imageCount(imageList.count())
        val images = imageList.map { DetailedImageData(it) }
        imageAdepter.submitList(images)

    }
    // 상품 사진 등록 개수 입력 함수
    private fun imageCount(count: Int) {
        binding.productImageCountTextView.text = getString(R.string.image_count, count, 5)
    }

    // 맵 설정 함수
    private fun settingMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 사진 찍고 이미지 비트맵으로 저장 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.FLAG_REQ_CAMERA -> {
                    realUri?.let { uri ->
                        imageList.add(uri.toString())
                        updateImageList()
                    }
                }

            }
        }
    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 기본 위치 설정 (예시로 서울의 위도, 경도를 설정)
        val initialLocation = LatLng(latitude, longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        val uiSettings: UiSettings = googleMap.uiSettings
        uiSettings.setScrollGesturesEnabled(false)
        val markerOptions = MarkerOptions()
            .position(initialLocation)
            .title("공유 희망 장소")
        val zoomLevel = 16.0f
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, zoomLevel))

        marker = googleMap.addMarker(markerOptions) ?: return

    }


}