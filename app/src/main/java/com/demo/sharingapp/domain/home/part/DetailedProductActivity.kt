package com.demo.sharingapp.domain.home.part

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityDetailedProductBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData
import com.demo.sharingapp.domain.home.part.data.DetailedProductData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayoutMediator
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailedProductActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityDetailedProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 맵 설정
        initMap()

        // 서버에서 상품 데이터 받아오기
        getDetailedData()

    }

    // 맵 설정 함수
    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapDetailed) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 서버에서 상품 데이터 받아오기 함수
    private fun getDetailedData() {
        val productId = intent.getLongExtra("productId", 140)
        val accessToken = SharedPreferencesData.getData(this, ACCESS_TOKEN)
        RetrofitManager.instance.getDetailedProduct(this, productId, accessToken) {
            // 상품 데이터 설정 함수
            settingData(it)
        }
    }

    // 상품 데이터 설정 함수
    private fun settingData(it: DetailedProductData) {
        //가격 변환
        val currencyFormat = NumberFormat.getInstance(Locale.KOREA)
        val buyPrice = currencyFormat.format(it.buyPrice)
        val sharePrice = currencyFormat.format(it.sharePrice)

        // 구매일자 변환
        val inputDateTime = LocalDateTime.parse(it.buyDate)
        val outputDateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd(EEE)", Locale.KOREA)
        val formattedDate = inputDateTime.format(outputDateFormat)

        // 상품 이미지
        val images = it.images
            .map { uriString -> DetailedImageData(uriString) }
        val frameAdapter = FrameAdapter(images)
        binding.productImageViewPager.adapter = frameAdapter

        // 프로필 사진
        Glide.with(binding.profileImageView)
            .load(it.sellerProfile)
            .circleCrop()
            .into(binding.profileImageView)

        showLocationOnMap(it.latitude, it.longitude)


        // 유저 닉네임
        binding.userNicknameTextView.text = it.sellerNickname

        // 상품 제목
        binding.productTitleTextView.text = it.title

        // 구매 일자
        binding.buyDateTextView.text = formattedDate

        // 좋아요 유무
        if (it.checkLiked) { // 좋아요가 true 일때
            binding.likeImageView.setImageResource(R.drawable.heart_fill)
        } else { // 좋아요가 false 일때
            binding.likeImageView.setImageResource(R.drawable.heart)
        }

        // 좋아요 개수
        binding.likeCountTextView.text = it.likeCount.toString()

        // 구입가격
        binding.buyPriceTextView.text = getString(R.string.buy_price, buyPrice)

        // 구입 수량
        binding.buyCountTextView.text = getString(R.string.product_count, it.buyCount)

        // 공유 가격
        binding.sharePriceTextView.text = getString(R.string.buy_price, sharePrice)

        // 공유 수량
        binding.shareCountTextView.text = getString(R.string.product_count, it.shareCount)

        // 상세 설명
        binding.descriptionTextView.text = it.description

        TabLayoutMediator(
            binding.tabLayout,
            binding.productImageViewPager,
        ) { tab, position ->
            binding.productImageViewPager.currentItem = tab.position
        }.attach()
    }

    // 구글맵 위치 변환
    private fun showLocationOnMap(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(location)
            .title("공유 희망 장소")
        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17f))
    }

    //구글맵 초기 설정
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 기본 위치 설정 (예시로 서울의 위도, 경도를 설정)
        val initialLocation = LatLng(37.5665, 126.9780)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        val uiSettings: UiSettings = googleMap.uiSettings
        uiSettings.setScrollGesturesEnabled(false)

    }
}