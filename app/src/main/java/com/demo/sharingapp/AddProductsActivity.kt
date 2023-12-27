package com.demo.sharingapp

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.addproduct.AddProductImageData
import com.demo.sharingapp.addproduct.ProductImageAdapter
import com.demo.sharingapp.databinding.ActivityAddProductsBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.product.ProductBottomSheet
import com.demo.sharingapp.domain.product.ProductViewModel
import com.demo.sharingapp.login.data.AccessTokenRequest
import com.demo.sharingapp.login.data.Products
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.FIND_LATITUDE
import com.demo.sharingapp.utils.Constants.FIND_LONGITUDE
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AddProductsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener {

    // 뷰모델
    private lateinit var productViewModel: ProductViewModel

    // 구글맵
    private lateinit var mMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var buyDate: LocalDateTime

    // 이미지
    private lateinit var imageAdepter: ProductImageAdapter
    private var imageList: ArrayList<Uri> = ArrayList()

    // 달력
    private var calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH) + 1
    private var day = calendar.get(Calendar.DAY_OF_MONTH)

    // 상품 데이터
    private var latitude: Double = 36.0
    private var longitude: Double = 127.5
    private var title = ""
    private var buyPrice = ""
    private var buyCount = ""
    private var sharePrice = ""
    private var shareCount = ""
    private var categoryId = 0
    private var description = ""

    private lateinit var binding: ActivityAddProductsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카테고리 선택 함수 호출
        categoryCheck()

        // 위치 데이터 불러 오기 함수 호출
        initPlaceData()

        // 불러온 데이터 넣기 함수 호출
        puttingData()

        // 이전버튼 클릭 함수 호출
        movePrevious()

        // 맵 설정 함수 호출
        settingMap()

        // 크게보기 클릭 함수 호출
        bigSizeMapButtonClick()

        // 초기 리사이클러뷰 설정
        initRecyclerView()

        // 상품이미지 추가 버튼 함수 호출
        clickAddProductButton()

        // 이미지 데이터 불러오기 함수 호출
        getImageDate()

        // 날짜텍스트 클릭 함수 호출
        clickBuyDateButton()

        // 다음 버튼 클릭 함수 호출
        nextButtonClick()

    }

    // 다음 버튼 클릭 함수
    private fun nextButtonClick() {

        // 다음 버튼 클릭 시
        binding.addProductNextButton.setOnClickListener {

            // editText 비어있는지 확인
            val checkEditText = allCheckEditText()

            // 비어있는 않는다면
            if (checkEditText) {

                // 뷰모델로 데이터 보내는 함수 호출
                sendViewModel()

                // 바텀시트 설정
                val bottomSheetFragment = ProductBottomSheet()
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

            } else {
                Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show()
            }


        }
    }

    // 뷰모델로 데이터 보내는 함수
    private fun sendViewModel() {
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val imageFileList: ArrayList<MultipartBody.Part> = ArrayList()
        imageList.forEach {
            val file = File(cacheDir, "image.jpeg")
            val fileOutputStream = FileOutputStream(file)
            convertUriToJpeg(it).compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("photos", file.name, requestFile)
            Log.e("aa", imagePart.body.contentType().toString())
            imageFileList.add(imagePart)
        }
        productViewModel.updateProductImage(imageFileList)

        // 뷰모델에 데이터 업데이트
        productViewModel.updateProductImage(
            title = title,
            buyPrice = buyPrice.toInt(),
            buyCount = buyCount.toInt(),
            sharePrice = sharePrice.toInt(),
            shareCount = shareCount.toInt(),
            description = description,
            longitude = longitude,
            latitude = latitude,
            categoryId = categoryId,
            buyDate = getString(R.string.buy_date_local, year, month, day)
        )

    }

    // uri를 비트맵으로 바꾸는 함수
    private fun convertUriToJpeg(uri: Uri): Bitmap {
        val input = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return bitmap
    }

    // 모든 빈칸 채워져있는지 확인하는 함수
    private fun allCheckEditText(): Boolean {
        inputDate()
        var checkEditText = true

        // 제목 비어있는지 확인
        if (title == "") {
            binding.titleEditTextError.isVisible = true
            checkEditText = false
        } else {
            binding.titleEditTextError.isVisible = false
        }

        // 구입 비어있는지 확인
        if (buyPrice == "" || buyCount == "") {
            binding.purchaseEditTextError.isVisible = true
            checkEditText = false
        } else {
            binding.purchaseEditTextError.isVisible = false
        }

        // 공유 비어있는지 확인
        if (sharePrice == "" || shareCount == "") {
            binding.shareEditTextError.isVisible = true
            checkEditText = false
        } else {
            binding.shareEditTextError.isVisible = false
        }

        // 카테고리 비어있는지 확인
        if (categoryId == 0) {
            binding.categoryError.isVisible = true
            checkEditText = false
        } else {
            binding.categoryError.isVisible = false
        }

        // 카테고리 비어있는지 확인
        if (description == "") {
            binding.descriptionEditTextError.isVisible = true
            checkEditText = false
        } else {
            binding.descriptionEditTextError.isVisible = false
        }

        return checkEditText
    }


    // 이미지 데이터 불러오기 함수
    private fun getImageDate() {
        if (imageList != null) {
            imageList = (intent.getSerializableExtra("imageList") ?: imageList) as ArrayList<Uri>
            updateImageList()
        } else {
            imageCount(0)
        }
    }

    // 날짜텍스트 클릭 함수
    private fun clickBuyDateButton() {
        binding.buyDateTextView.setOnClickListener {
            this?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    run {
                        binding.buyDateTextView.setText(year.toString() + "." + (month + 1).toString() + "." + day.toString())
                        this.year = year
                        this.month = month + 1
                        this.day = day
                    }
                }, year, month - 1, day)
            }?.show()
        }
    }

    // 노컬 데이트 타임형으로 변형 함수
    private fun makeLocalDateTime() {
        val dateString = "$year-$month-${day}T15:30:00"
        val format = "yyyy-MM-dd'T'HH:mm:ss" // 날짜 형식에 맞게 수정
        buyDate = parseDateStringToDateTime(dateString, format) ?: return

        if (buyDate != null) {
            println("변환된 날짜 및 시간: $buyDate")
        } else {
            println("날짜 변환 실패")
        }
    }


    // 날짜 로컬데이트타임으로 형 변환
    private fun parseDateStringToDateTime(dateString: String, format: String): LocalDateTime? {
        val formatter = DateTimeFormatter.ofPattern(format)
        return try {
            LocalDateTime.parse(dateString, formatter)
        } catch (e: Exception) {
            null // 날짜 파싱 실패 시 null 반환
        }
    }

    // 카테고리 선택 함수
    private fun categoryCheck() {

        // 카테고리 채소 클릭
        binding.chip1.setOnClickListener {
            clearChip()
            binding.chip1.isChecked = true
            categoryId = 1
        }

        // 카테고리 과일 클릭
        binding.chip2.setOnClickListener {
            clearChip()
            binding.chip2.isChecked = true
            categoryId = 2
        }

        // 카테고리 간편식 클릭
        binding.chip3.setOnClickListener {
            clearChip()
            binding.chip3.isChecked = true
            categoryId = 3
        }

        // 카테고리 정육 클릭
        binding.chip4.setOnClickListener {
            clearChip()
            binding.chip4.isChecked = true
            categoryId = 4
        }

        // 카테고리 수산물 클릭
        binding.chip5.setOnClickListener {
            clearChip()
            binding.chip5.isChecked = true
            categoryId = 5
        }

        // 카테고리 기타 클릭
        binding.chip6.setOnClickListener {
            clearChip()
            binding.chip6.isChecked = true
            categoryId = 6
        }
    }

    // 칩 선택 초기화
    private fun clearChip() {
        binding.chip1.isChecked = false
        binding.chip2.isChecked = false
        binding.chip3.isChecked = false
        binding.chip4.isChecked = false
        binding.chip5.isChecked = false
        binding.chip6.isChecked = false
    }

    // 상품 사진 등록 개수 입력 함수
    private fun imageCount(count: Int) {
        binding.productImageCountTextView.text = getString(R.string.image_count, count, 5)
    }

    // 상품이미지 추가 버튼 함수
    private fun clickAddProductButton() {
        binding.addProductImageButton.setOnClickListener {

            // 상품이미지 추가 클릭시 나오는 메뉴 함수 호출
            addProductButtonMenu(it, R.menu.menu_add_product)
        }
    }

    // 카메라 이동하는 함수
    private fun openCamera() {
        val cameraPermission = arrayOf(Manifest.permission.CAMERA)
        if (PermissionUtil.checkPermission(this, cameraPermission)) {
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, Constants.FLAG_REQ_CAMERA)
        } else {
            PermissionUtil.requestPermission(this, cameraPermission)
        }
    }

    // 사진 찍고 이미지 비트맵으로 저장 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        //카메라로 방금 촬영한 이미지를 미리 만들어 놓은 이미지뷰로 전달 합니다.
                        val bitmap = data?.extras?.get("data") as Bitmap

                        val imageUri = saveBitmapToGallery(this, bitmap)
                        if (imageUri != null) {
                            imageList.add(imageUri)
                            updateImageList()
                        }

                    }
                }
                300 -> {
                    latitude = data?.getDoubleExtra("latitude", 0.0) ?: return
                    longitude = data?.getDoubleExtra("longitude", 0.0) ?: return

                    saveSharedString(FIND_LONGITUDE, longitude.toString())
                    saveSharedString(FIND_LATITUDE, latitude.toString())

                    Log.e("SendingActivity","현제 ${SharedPreferencesData.getData(this, LATITUDE)} 바꾼 ${SharedPreferencesData.getData(this,
                        FIND_LATITUDE)}")
                    // 결과 데이터 사용
                    Log.d("SendingActivity", "Received result: $latitude, $longitude")
                    if (latitude != null && longitude != null)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,
                            longitude), 15f))
                    marker.position = mMap.cameraPosition.target

                }
            }
        }
    }

    private fun saveSharedString(title: String, data: String) {
        SharedPreferencesData.saveData(this, title, data)
    }

    // 비트맵 이미지를 갤러리에 저장하는 함수
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, generateFileName())
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues)
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

    // 갤러리에 저장할때 이미지 이름 정하는 함수
    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "JPEG_$timeStamp.jpeg"
    }

    // 겔러리 이동 함수
    private fun moveGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        // 이미지 여러장 저장
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activityResult.launch(intent)
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

    // 초기 리사이클러뷰 설정 함수
    private fun initRecyclerView() {

        // 리사이클러뷰 안에 뷰 클릭 시 뷰 삭제
        imageAdepter = ProductImageAdapter { postion ->
            imageList.removeAt(postion)
            //이미지 어댑터에서 업데이트 함수 호출
            updateImageList()
            imageCount(imageList.count())
        }
        // 리사이클러뷰 어댑터와 레이아웃메니져 설정
        binding.productImageRecyclerView.apply {
            adapter = imageAdepter
            layoutManager = LinearLayoutManager(this@AddProductsActivity).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
    }

    // 맵 설정 함수
    private fun settingMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 크게보기 클릭 함수
    private fun bigSizeMapButtonClick() {
        binding.mapBigSizeButton.setOnClickListener {
            // 작서한 데이터 저장하고 화면 이동 함수 호출
            saveDataAndMove()
        }
    }

    // 이전버튼 눌렀을 때 함수
    private fun movePrevious() {
        binding.movePreviousButton.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
            removeFindPlace()
            finish()
        }
    }

    // 불러온 데이터 넣기 함수
    private fun puttingData() {
        binding.titleEditText.setText(title)
        binding.purchasePriceEditText.setText(buyPrice)
        binding.purchasingVolumeEditText.setText(buyCount)
        binding.sharePriceEditText.setText(sharePrice)
        binding.shareVolumeEditText.setText(shareCount)
        binding.descriptionEditText.setText(description)
        binding.buyDateTextView.text = getString(R.string.buy_date, year, month, day)
        if (categoryId != 0) {
            when (categoryId) {
                1 -> binding.chip1.isChecked = true
                2 -> binding.chip2.isChecked = true
                3 -> binding.chip3.isChecked = true
                4 -> binding.chip4.isChecked = true
                5 -> binding.chip5.isChecked = true
                6 -> binding.chip6.isChecked = true
            }
        }

    }

    // 적은 데이터 불러오기 함수
    private fun initPlaceData() {
        latitude = SharedPreferencesData.getData(this@AddProductsActivity, LATITUDE).toDouble()
        longitude = SharedPreferencesData.getData(this@AddProductsActivity, LONGITUDE).toDouble()

        Log.e("SendingActivity","시작 위치 $latitude, $longitude")
//        title = intent.getStringExtra("title") ?: ""
//        buyPrice = intent.getStringExtra("buyPrice") ?: ""
//        buyCount = intent.getStringExtra("buyCount") ?: ""
//        sharePrice = intent.getStringExtra("sharePrice") ?: ""
//        shareCount = intent.getStringExtra("shareCount") ?: ""

//        categoryId = intent.getIntExtra("categoryId", 0)
//        description = intent.getStringExtra("description") ?: ""
//        year = intent.getIntExtra("year", year)
//        month = intent.getIntExtra("month", month)
//        day = intent.getIntExtra("day", day)
//        imageList = intent.getSerializableExtra("imageList")
    }

    // 작서한 데이터 저장하고 화면 이동 함수
    private fun saveDataAndMove() {

        startActivityForResult(Intent(this, BigSizeMapActivity::class.java), 300)
        // 입력한 데이터 넣기 함수호출
//        inputDate()
//        startActivity(Intent(this, BigSizeMapActivity::class.java).apply {
//            putExtra("title", title)
//            putExtra("buyPrice", buyPrice)
//            putExtra("buyCount", buyCount)
//            putExtra("sharePrice", sharePrice)
//            putExtra("shareCount", shareCount)
//            putExtra("latitude", latitude)
//            putExtra("longitude", longitude)
//            putExtra("imageList", imageList)
//            putExtra("categoryId", categoryId)
//            putExtra("description", description)
//            putExtra("year", year)
//            putExtra("month", month)
//            putExtra("day", day)
//        })

    }

    // 입력한 데이터 넣기 함수
    private fun inputDate() {
        title = binding.titleEditText.text.toString()
        buyPrice = binding.purchasePriceEditText.text.toString()
        buyCount = binding.purchasingVolumeEditText.text.toString()
        sharePrice = binding.sharePriceEditText.text.toString()
        shareCount = binding.shareVolumeEditText.text.toString()
        description = binding.descriptionEditText.text.toString()
    }


    // 맵 초기 위치와 마커 설정 함수
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(sydney)
            .title("공유 희망 장소")
        val zoomLevel = 16.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

        marker = mMap.addMarker(markerOptions) ?: return
        mMap.setOnCameraMoveListener(this)

    }

    // 맵 화면 이동할때 마다 좌표 알려주는 함수
    override fun onCameraMove() {
        val newLatLng = mMap.cameraPosition.target
        marker.position = newLatLng
        latitude = marker.position.latitude
        longitude = marker.position.longitude
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
                    val imageUrl = it.data!!.clipData!!.getItemAt(index).uri
                    imageList.add(imageUrl)
                }

            } else { // 이미지 한장 골랐을 때
                val imageUri = it.data!!.data
                imageList.add(imageUri!!)
            }

            //이미지 어댑터에서 업데이트 함수 호출
            updateImageList()


        }
    }

    //이미지 어댑터에서 업데이트 함수
    private fun updateImageList() {
        val images = imageList.map { AddProductImageData(it) }
        imageAdepter.submitList(images)

        // 선택한 이미지 개수 업데이트
        imageCount(imageList.count())
    }

    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    private fun removeFindPlace() {
        SharedPreferencesData.removeData(this, FIND_LATITUDE)
        SharedPreferencesData.removeData(this, FIND_LONGITUDE)
    }


}