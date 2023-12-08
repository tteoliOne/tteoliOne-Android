package com.demo.sharingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.demo.sharingapp.databinding.ActivityBigSizeMapBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.ACCESS_FINE_LOCATION_CODE
import com.demo.sharingapp.utils.Constants.FIND_LATITUDE
import com.demo.sharingapp.utils.Constants.FIND_LONGITUDE
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class BigSizeMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener {

    private lateinit var mMap: GoogleMap

    private lateinit var marker: Marker

    var latitude: Double = 42.0
    var longitude: Double = 127.5



    private var locationPermissionGranted = false


    private lateinit var binding: ActivityBigSizeMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBigSizeMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPlace(FIND_LATITUDE) && checkPlace(FIND_LONGITUDE)){
            latitude = SharedPreferencesData.getData(this, Constants.FIND_LATITUDE).toDouble()
            longitude = SharedPreferencesData.getData(this, Constants.FIND_LONGITUDE).toDouble()
        }
        else if (checkPlace(LATITUDE) && checkPlace(LONGITUDE)){
            latitude = SharedPreferencesData.getData(this, Constants.LATITUDE).toDouble()
            longitude = SharedPreferencesData.getData(this, Constants.LONGITUDE).toDouble()
        }


        val title = intent.getStringExtra("title")
        val buyPrice = intent.getStringExtra("buyPrice")
        val buyCount = intent.getStringExtra("buyCount")
        val sharePrice = intent.getStringExtra("sharePrice")
        val shareCount = intent.getStringExtra("shareCount")
        val imageList = intent.getSerializableExtra("imageList")
        val categoryId = intent.getIntExtra("categoryId", 0)
        val description = intent.getStringExtra("description")
        val year = intent.getIntExtra("year", 0)
        val month = intent.getIntExtra("month", 0)
        val day = intent.getIntExtra("day", 0)

        // 내 위치 이동 버튼
        binding.myPlaceButton.setOnClickListener {

            // 권한 체크 함수 호출
            checkPermissionLocation()

        }


        // 완료 버튼
        binding.completeButton.setOnClickListener {


        }


        binding.allMapCloseButton.setOnClickListener {
//            startActivity(Intent(this, AddProductsActivity::class.java).apply {
//                putExtra("title", title)
//                putExtra("buyPrice", buyPrice)
//                putExtra("buyCount", buyCount)
//                putExtra("sharePrice", sharePrice)
//                putExtra("shareCount", shareCount)
//                putExtra("latitude", latitude)
//                putExtra("longitude", longitude)
//                putExtra("imageList", imageList)
//                putExtra("categoryId", categoryId)
//                putExtra("description", description)
//                putExtra("year", year)
//                putExtra("month", month)
//                putExtra("day", day)
//
//            })
            val resultIntent = Intent()
            resultIntent.putExtra("latitude", latitude)
            resultIntent.putExtra("longitude", longitude)
            Log.e("SendingActivity","$latitude, $longitude")
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

                // 현제 위치로 이동 함수 호출
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
                    ACCESS_FINE_LOCATION_CODE)
            }
        }
    }

    // 현제 위치로 이동 함수
    @SuppressLint("MissingPermission")
    private fun getCurrentPlace() {
        // 안드로이드 위치 api
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            Log.e("location", it.longitude.toString())
            longitude = it.longitude
            latitude = it.latitude
            val currentPlace = LatLng(it.latitude, it.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPlace, 17.0f))
            marker.position = currentPlace
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한이 있는지 확인
        locationPermissionGranted = requestCode == ACCESS_FINE_LOCATION_CODE &&
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

    // 권한이 필요한지 알려주는 다이얼로그 함수
    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한을 켜주셔야지 내 위치 불러오기가 가능합니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION_CODE)
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun checkPlace(place:String):Boolean{
        return SharedPreferencesData.containsData(this,place)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(sydney)
            .title("공유 희망 장소")

        val zoomLevel = 17.0f

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

        marker = mMap.addMarker(markerOptions) ?: return
        mMap.setOnCameraMoveListener(this)

        if (locationPermissionGranted) {
            //mMap.isMyLocationEnabled = true
        }
    }

    override fun onCameraMove() {
        val newLatLng = mMap.cameraPosition.target
        marker.position = newLatLng
        latitude = marker.position.latitude
        longitude = marker.position.longitude

        Log.e("SendingActivity","$latitude, $longitude")
    }


}