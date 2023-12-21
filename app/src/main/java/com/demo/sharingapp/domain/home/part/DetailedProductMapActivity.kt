package com.demo.sharingapp.domain.home.part

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityDetailedProductMapBinding
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailedProductMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityDetailedProductMapBinding

    var latitude = 0.0
    var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 내위치
        latitude = intent.getDoubleExtra(LATITUDE,latitude)
        longitude = intent.getDoubleExtra(LONGITUDE,longitude)

        // 이전 버튼 클릭
        binding.completeButton.setOnClickListener {
            finish()
        }

        // 맵 설정
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.allMapDetailed) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val initialLocation = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(initialLocation)
            .title("공유 희망 장소")

        // 기본 위치 설정 (예시로 서울의 위도, 경도를 설정)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15f))
        googleMap.addMarker(markerOptions)

        // 현재 위치 레이어 활성화
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}