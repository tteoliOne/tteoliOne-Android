package com.demo.sharingapp.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.MainActivity
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityUserPlaceBinding
import com.demo.sharingapp.login.address.AddressAPIClient
import com.demo.sharingapp.login.address.AddressService
import com.demo.sharingapp.login.address.UserDto
import com.demo.sharingapp.login.data.AddressRequest
import com.demo.sharingapp.retrofit.RestAPI
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class UserPlace : AppCompatActivity() {

    // 현제 위치 권한 여부 확인 변수
    private var locationPermissionGranted = false
    private lateinit var addressAdepter: UserPlaceAdepter
    // 검색창 입력 단어
    private var searchFor: String = ""
    // 핸들러
    private val handler = Handler(Looper.getMainLooper())


    private lateinit var binding: ActivityUserPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 리사이클러뷰 초기 설정
        initRecyclerView()

        // 검색버튼 클릭 함수 호출
        deleteButtonClick()

        val runnable = Runnable {
            searchAddress()
        }

        binding.cancelButton.setOnClickListener {
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
        }

        binding.addressEditText.addTextChangedListener {
            searchFor = it.toString()
            handler.removeCallbacks(runnable)
            handler.postDelayed(
                runnable,
                300
            )
        }

        // 현 위치 찾기 버튼 클릭
        clickMyPlaceButton()

    }

    // 현 위치 찾기 버튼 클릭 함수
    private fun clickMyPlaceButton() {
        binding.myPlaceButton.setOnClickListener {
            checkPermissionLocation()
        }
    }

    // 리사이클러뷰 초기 설정 함수
    private fun initRecyclerView() {
        addressAdepter = UserPlaceAdepter() {
            geoCoding(it)
        }

        binding.addressListRecyclerView.apply {
            adapter = addressAdepter
            layoutManager = LinearLayoutManager(this@UserPlace)
        }
    }

    // 검색버튼 클릭 함수
    private fun deleteButtonClick() {

        // 검색 버튼 클릭 시
        binding.deleteButton.setOnClickListener {
            Log.e("aa","cancel")
            binding.addressEditText.text.clear()
        }
    }

    // 주소 검색 api
    private fun searchAddress() {
        val searchValue = binding.addressEditText.text

        // 레트로핏
        val retrofit = Retrofit.Builder().baseUrl("https://business.juso.go.kr/")
            .addConverterFactory(GsonConverterFactory.create()).build();
        val service = retrofit.create(RestAPI::class.java)
        service.getAddress("U01TX0FVVEgyMDIzMTEwNzIwNTk1NTExNDI1MzU=",
            searchValue.toString(),
            "json").enqueue(object : Callback<AddressRequest> {
            override fun onResponse(
                call: Call<AddressRequest>,
                response: Response<AddressRequest>,
            ) {
                if (response.isSuccessful) {
                    Log.e("address", response.body().toString())
                    val data = response.body()?.results?.juso
                    addressAdepter.submitList(data)
                } else {
                    Log.e("address", "실패")
                }
            }

            override fun onFailure(call: Call<AddressRequest>, t: Throwable) {
                Log.e("address", t.message.toString())
            }
        })
    }

    // 권한 체크 함수
    private fun checkPermissionLocation() {
        when {
            // 권한이 있는지 확인

            // 권한이 있을 때
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 있으므로 액션 실행
                getCurrentPlace()
            }

            // 왜 필요한지 한번도 설명
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationalDialog()
            }
            else -> {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.ACCESS_FINE_LOCATION_CODE)
            }
        }
    }

    // 권한이 필요한지 알려주고 권한 설정으로 이동 여부 다이얼로그 함수
    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한을 켜주셔야지 내 위치 불러오기가 가능합니다. 앱 설정 화면으로 진입하셔 권한을 켜주세요")
            .setPositiveButton("권한 변경하러 가기") { _, _ ->

                // 권한 설정 화면으로 이동하는 함수 호출
                navigateToAppSetting()
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    // 권한 설정 화면으로 이동하는 함수
    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한이 있는지 확인
        locationPermissionGranted = requestCode == Constants.ACCESS_FINE_LOCATION_CODE &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        // 권한 있을 시
        if (locationPermissionGranted) {

            // 현제 위치로 이동 함수 호출
            getCurrentPlace()

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                showPermissionRationalDialog()
            } else {
                showPermissionSettingDialog()
            }
        }
    }

    // 권한이 필요한지 알려주는 다이얼로그 함수
    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한을 켜주셔야지 내 위치 불러오기가 가능합니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.ACCESS_FINE_LOCATION_CODE)
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    // 현제 위치로 이동 함수
    @SuppressLint("MissingPermission")
    private fun getCurrentPlace() {
        // 안드로이드 위치 api
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            location?.let {
                Log.e("place",it.toString())

                savePlace(it.longitude,it.latitude)
                // mainActivity 로 위치정보와 함께 이동하는 함수 호출
                moveMainActivity(it.latitude, it.longitude)
            } ?: kotlin.run {
                Log.e("place", "Location is null")
            }



        }
    }

    // mainActivity 로 위치정보와 함께 이동하는 함수
    private fun moveMainActivity(latitude: Double, longitude: Double) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(LATITUDE, latitude)
            putExtra(LONGITUDE, longitude)
        }
        startActivity(intent)
    }


    // 주소 api에서 받아오기
    private fun listAddress(address: String) {
        val addressService = AddressAPIClient.retrofit.create(AddressService::class.java)
        addressService.listAddress("U01TX0FVVEgyMDIzMTEwNzIwNTk1NTExNDI1MzU=",

            address,
            "json")
            .enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    Log.e("MainActivity", response.body().toString())


                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {

                }

            })
    }

    // 주소에서 좌표바꾸는 함수
    private fun geoCoding(address: String) {
        try {
            Geocoder(this, Locale.KOREA).getFromLocationName(address, 1)?.let {
                Location("").apply {
                    latitude = it[0].latitude
                    longitude = it[0].longitude
                    savePlace(longitude,latitude)
                    moveMainActivity(latitude,longitude)
                }
            } ?: Location("").apply {
                latitude = 0.0
                longitude = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("adress", e.toString())
        }
    }

    // 내부 저장소에 위치 값 저장
    private fun savePlace(longitude: Double,latitude: Double){
        SharedPreferencesData.saveData(this, LONGITUDE,longitude.toString())
        SharedPreferencesData.saveData(this, LATITUDE,latitude.toString())
    }
}