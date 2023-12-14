package com.demo.sharingapp.domain.home.part

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityDetailedProductBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData
import com.demo.sharingapp.domain.home.part.data.DetailedProductData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayoutMediator
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailedProductActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityDetailedProductBinding

    private var liked = false

    private var likePoint = 0

    private var uri = ""

    private var latitude = 0.0
    private var longitude =0.0
    private var checkOwner = false

    lateinit var productData: DetailedProductData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productId = intent.getLongExtra(PRODUCT_ID,140)
        val accessToken = SharedPreferencesData.getData(this, ACCESS_TOKEN)


        // 맵 설정
        initMap()

        // 서버에서 상품 데이터 받아오기
        getDetailedData()

        // 좋아요 클릭
        clickLiked(productId, accessToken)

        // 이전 버튼 클릭
        clickBackButton()

        // 영수증 클릭
        clickReceipt()

        binding.settingMenu.setOnClickListener {
            if (binding.menuDecoratingImageView.getVisibility() == View.VISIBLE){
                binding.menuDecoratingImageView.isVisible= false
                binding.menuRemoveButton.isVisible=false
                binding.menuModifyButton.isVisible=false
                binding.menuReportButton.isVisible = false
            }
            else if(checkOwner){
                binding.menuDecoratingImageView.isVisible= true
                binding.menuRemoveButton.isVisible=true
                binding.menuModifyButton.isVisible=true
            }else{
                binding.menuDecoratingImageView.isVisible= true
                binding.menuReportButton.isVisible = true
            }
        }

        binding.menuRemoveButton.setOnClickListener {
            binding.menuDecoratingImageView.isVisible= false
            binding.menuRemoveButton.isVisible=false
            binding.menuModifyButton.isVisible=false
            binding.menuReportButton.isVisible = false
        }
        binding.menuModifyButton.setOnClickListener {
            binding.menuDecoratingImageView.isVisible= false
            binding.menuRemoveButton.isVisible=false
            binding.menuModifyButton.isVisible=false
            binding.menuReportButton.isVisible = false
        }
        binding.menuReportButton.setOnClickListener {
            binding.menuDecoratingImageView.isVisible= false
            binding.menuRemoveButton.isVisible=false
            binding.menuModifyButton.isVisible=false
            binding.menuReportButton.isVisible = false
        }

    }

    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

   private fun clickReceipt() {
        binding.receiptImageView.setOnClickListener {
            showDialog(uri)
        }
    }

    private fun clickLiked(productId: Long, accessToken: String) {
        binding.likeImageView.setOnClickListener {
            if (liked) {
                binding.likeImageView.setImageResource(R.drawable.heart)
                likePoint -= 1
                binding.likeCountTextView.text = likePoint.toString()
                liked = !liked

            } else {
                binding.likeImageView.setImageResource(R.drawable.heart_fill)
                likePoint += 1
                binding.likeCountTextView.text = likePoint.toString()
                liked = !liked
            }
            likeClick(productId, accessToken)
        }
    }

    // 알림창 띄우기
    private fun showDialog(uri: String) {
        val dialog = DetailedProductDialog(uri)
        Log.e("uri",uri.toString())
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager,
            "SignupDialog")
    }

    // 좋아요 클릭 시 함수
    private fun likeClick(it: Long, accessToken: String) {
        RetrofitManager.instance.postProductLike(this, it, accessToken)
    }

    // 맵 설정 함수
    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapDetailed) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 서버에서 상품 데이터 받아오기 함수
    private fun getDetailedData() {
        val productId = intent.getLongExtra(PRODUCT_ID,140)
        val accessToken = SharedPreferencesData.getData(this, ACCESS_TOKEN)

        RetrofitManager.instance.getDetailedProduct(this, productId, accessToken) {
            // 상품 데이터 설정 함수
            settingData(it)
        }
    }

    // 상품 데이터 설정 함수
    private fun settingData(it: DetailedProductData) {

        productData = it

        checkOwner = it.checkOwner
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
        val frameAdapter = FrameAdapter(images){
            Log.e("aa","쿨릭")
            val intent = Intent(this,DetailedProductImageActivity::class.java)
                .putExtra("data",it.images)
            startActivity(intent)

        }
        binding.productImageViewPager.adapter = frameAdapter


        //
        uri = it.receipt

        // 프로필 사진
        Glide.with(binding.profileImageView)
            .load(it.sellerProfile)
            .circleCrop()
            .into(binding.profileImageView)

        showLocationOnMap(it.latitude, it.longitude)
        latitude = it.latitude
        longitude = it.longitude


        // 유저 닉네임
        binding.userNicknameTextView.text = it.sellerNickname

        // 상품 제목
        binding.productTitleTextView.text = it.title

        // 구매 일자
        binding.buyDateTextView.text = formattedDate

        // 좋아요 유무
        liked = it.checkLiked
        if (liked) { // 좋아요가 true 일때
            binding.likeImageView.setImageResource(R.drawable.heart_fill)
        } else { // 좋아요가 false 일때
            binding.likeImageView.setImageResource(R.drawable.heart)
        }

        // 좋아요 개수
        likePoint = it.likeCount
        binding.likeCountTextView.text = likePoint.toString()

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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))


    }


    //구글맵 초기 설정
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 기본 위치 설정 (예시로 서울의 위도, 경도를 설정)
        val initialLocation = LatLng(37.5665, 126.9780)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        val uiSettings: UiSettings = googleMap.uiSettings
        uiSettings.setScrollGesturesEnabled(false)


        // 맵에 터치 리스너 설정
        googleMap.setOnMapClickListener(this)

    }

    // 맵 클릭시
    override fun onMapClick(p0: LatLng) {
        val intent= Intent(this, DetailedProductMapActivity::class.java)
            .putExtra(LATITUDE,latitude)
            .putExtra(LONGITUDE,longitude)
        Log.e("demap","${latitude} ${longitude}")
        startActivity(intent)
    }

}