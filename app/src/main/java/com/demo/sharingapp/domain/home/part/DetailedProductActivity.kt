package com.demo.sharingapp.domain.home.part

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.demo.sharingapp.AddProductsActivity
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityDetailedProductBinding
import com.demo.sharingapp.domain.chat.chatroom.ChatRoomActivity
import com.demo.sharingapp.domain.home.part.data.DetailedImageData
import com.demo.sharingapp.domain.home.part.data.DetailedProductData
import com.demo.sharingapp.domain.other_profile.OtherProfileActivity
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.CHATROOM_NUMBER
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.MOVE_MODIFY_CODE
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_COUNT
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_DAY
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_MONTH
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_PRICE
import com.demo.sharingapp.utils.Constants.PRODUCT_BUY_YEAR
import com.demo.sharingapp.utils.Constants.PRODUCT_CATEGORY_ID
import com.demo.sharingapp.utils.Constants.PRODUCT_DESCRIPTION
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.PRODUCT_IMAGE
import com.demo.sharingapp.utils.Constants.PRODUCT_LATITUDE
import com.demo.sharingapp.utils.Constants.PRODUCT_LONGITUDE
import com.demo.sharingapp.utils.Constants.PRODUCT_RECEIPT_IMAGE
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_COUNT
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_PRICE
import com.demo.sharingapp.utils.Constants.PRODUCT_TITLE
import com.demo.sharingapp.utils.Constants.PRODUCT_TYPE
import com.demo.sharingapp.utils.Constants.SELLER_ID
import com.demo.sharingapp.utils.Constants.USER_PROFILE
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonParser
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class DetailedProductActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityDetailedProductBinding

    //
    private lateinit var stompConnection: Disposable
    private lateinit var topic: Disposable

    private lateinit var accessToken: String

    private var liked = false

    private var likePoint = 0

    private var receiptUri = ""

    private var productId: Long = 0
    private var categoryId: Int = 0

    private var latitude = 0.0
    private var longitude = 0.0
    private var checkOwner = false

    private var productTitle = ""
    private var productBuyPrice = ""
    private var productBuyCount = ""
    private var productSharePrice = ""
    private var productShareCount = ""
    private var productBuyYear = 0
    private var productBuyMonth = 0
    private var productBuyDay = 0
    private var productDescription = ""
    private var sendUserNickname = ""
    private var profile = ""
    private var sellerId = 0L

    private lateinit var productImageArray: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId = intent.getLongExtra(PRODUCT_ID, 0)
        accessToken = SharedPreferencesData.getData(this, ACCESS_TOKEN)

        binding.callButton.setOnClickListener {
            RetrofitManager.instance.postChatRoom(this,productId){
                val intent = Intent(this,ChatRoomActivity::class.java)
                    .putExtra(CHATROOM_NUMBER,it.chatId.toString())
                    .putExtra(PRODUCT_ID, productId)
                    .putExtra(USER_PROFILE, profile)
                startActivity(intent)
            }


        }

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

        binding.profileImageView.setOnClickListener {
            val intent = Intent(this,OtherProfileActivity::class.java)
                .putExtra(SELLER_ID,sellerId)
            startActivity(intent)

        }

        binding.settingMenu.setOnClickListener {
            val popup = PopupMenu(this@DetailedProductActivity, it)
            if (checkOwner) { // 작성자 일때
                popup.menuInflater.inflate(R.menu.menu_detailed_my_product, popup.menu)
            } else {
                popup.menuInflater.inflate(R.menu.menu_detalied_other_product, popup.menu)
            }
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.modifyMenu -> {
                        // 메뉴 수정하기 클릭
                        clickModifyMenu()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.removeMenu -> { // 삭제하기
                        RetrofitManager.instance.getRemoveProduct(this, productId)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.reportMenu -> { // 신고하기
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popup.show()
        }

    }

    // 메뉴 수정하기 클릭 함수
    private fun clickModifyMenu() {
        val intent = Intent(this, AddProductsActivity::class.java).apply {
            putExtra(PRODUCT_TITLE, productTitle)
            putExtra(PRODUCT_BUY_PRICE, productBuyPrice)
            putExtra(PRODUCT_BUY_COUNT, productBuyCount)
            putExtra(PRODUCT_SHARE_PRICE, productSharePrice)
            putExtra(PRODUCT_SHARE_COUNT, productShareCount)
            putExtra(PRODUCT_BUY_YEAR, productBuyYear)
            putExtra(PRODUCT_BUY_MONTH, productBuyMonth)
            putExtra(PRODUCT_BUY_DAY, productBuyDay)
            putExtra(PRODUCT_DESCRIPTION, productDescription)
            putExtra(PRODUCT_LATITUDE, latitude)
            putExtra(PRODUCT_LONGITUDE, longitude)
            putExtra(PRODUCT_IMAGE, productImageArray)
            putExtra(PRODUCT_RECEIPT_IMAGE, receiptUri)
            putExtra(PRODUCT_TYPE, 1)
            putExtra(PRODUCT_ID, productId)
            putExtra(PRODUCT_CATEGORY_ID, categoryId)
        }
        startActivityForResult(intent, MOVE_MODIFY_CODE)
    }

    // 이전 버튼 클릭 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            // 이전 화면 이동
            moveBack()
        }
    }

    // 이전 화면 이동 함수
    private fun moveBack() {
        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    // 영수증 클릭 함수
    private fun clickReceipt() {
        binding.receiptImageView.setOnClickListener {
            showDialog(receiptUri)
        }
    }

    // 좋아요 클릭 함수
    private fun clickLiked(productId: Long, accessToken: String) {
        binding.likeImageView.setOnClickListener {
            if (liked) { // 좋아요 켜져있을 때
                binding.likeImageView.setImageResource(R.drawable.heart)
                likePoint -= 1
                binding.likeCountTextView.text = likePoint.toString()
                liked = !liked

            } else { // 좋아요 꺼져있을 때
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
        Log.e("uri", uri.toString())
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager,
            "SignupDialog")
    }

    // 좋아요 클릭 시 함수
    private fun likeClick(it: Long, accessToken: String) {
        RetrofitManager.instance.postProductLike(this, it)
    }

    // 맵 설정 함수
    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapDetailed) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 서버에서 상품 데이터 받아오기 함수
    private fun getDetailedData() {
        val productId = intent.getLongExtra(PRODUCT_ID, 140)
        val accessToken = SharedPreferencesData.getData(this, ACCESS_TOKEN)

        RetrofitManager.instance.getDetailedProduct(this, productId, accessToken) {
            // 상품 데이터 설정 함수
            settingData(it)
        }
    }

    // 상품 데이터 설정 함수
    private fun settingData(it: DetailedProductData) {

        checkOwner = it.checkOwner // 작성자 확인
        if (!checkOwner){
            binding.callButton.isVisible = true
        }

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
        val frameAdapter = FrameAdapter(images) { // 상품 이미지 클릭 했을때
            val intent = Intent(this, DetailedProductImageActivity::class.java)
                .putExtra("data", it.images)
            startActivity(intent)
        }
        binding.productImageViewPager.adapter = frameAdapter
        productImageArray = it.images

        //상대방 id
        sellerId = it.sellerId

        // 상품 id
        it.productId

        categoryId = it.categoryId

        // 영수증 이미지
        receiptUri = it.receipt

        // 프로필 사진
        Glide.with(binding.profileImageView)
            .load(it.sellerProfile)
            .circleCrop()
            .into(binding.profileImageView)

        profile = it.sellerProfile

        showLocationOnMap(it.latitude, it.longitude)
        latitude = it.latitude
        longitude = it.longitude


        // 유저 닉네임
        binding.userNicknameTextView.text = it.sellerNickname
        sendUserNickname = it.sellerNickname

        // 상품 제목
        binding.productTitleTextView.text = it.title
        productTitle = it.title

        // 구매 일자
        binding.buyDateTextView.text = formattedDate
        productBuyYear = inputDateTime.year
        productBuyMonth = inputDateTime.monthValue
        productBuyDay = inputDateTime.dayOfMonth


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
        productBuyPrice = it.buyPrice.toString()

        // 구입 수량
        binding.buyCountTextView.text = getString(R.string.product_count, it.buyCount)
        productBuyCount = it.buyCount.toString()

        // 공유 가격
        binding.sharePriceTextView.text = getString(R.string.buy_price, sharePrice)
        productSharePrice = sharePrice

        // 공유 수량
        binding.shareCountTextView.text = getString(R.string.product_count, it.shareCount)
        productShareCount = it.shareCount.toString()

        // 상세 설명
        binding.descriptionTextView.text = it.description
        productDescription = it.description


        // 테이블 레이아웃 설정
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
        val intent = Intent(this, DetailedProductMapActivity::class.java)
            .putExtra(LATITUDE, latitude)
            .putExtra(LONGITUDE, longitude)
        Log.e("demap", "${latitude} ${longitude}")
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MOVE_MODIFY_CODE && resultCode == Activity.RESULT_OK) {
            Log.e("시작", "시작")
            getDetailedData()
        }
    }


//    // 메뉴 버튼 함수
//    private fun menuButton() {
//        // 메뉴 버튼 클릭
//        clickMenuButton()
//
//        // 메뉴의 삭제하기 버튼 클릭
//        clickMenuRemoveButton()
//
//        // 메뉴의 수정하기 버튼 클릭
//        clickMenuModifyButton()
//
//        // 메뉴의 신고하기 버튼 클릭
//        clickMenuReportButton()
//    }

//    // 메뉴 버튼 클릭 함수
//    private fun clickMenuButton() {
//        binding.settingMenu.setOnClickListener {
//            if (binding.menuDecoratingImageView.getVisibility() == View.VISIBLE) { // 메뉴가 켜져있을 때
//                disappearsMenu()// 메뉴버튼 전체 사라짐
//            } else if (checkOwner) { // 작성자 일 때
//                binding.menuDecoratingImageView.isVisible = true
//                binding.menuRemoveButton.isVisible = true
//                binding.menuModifyButton.isVisible = true
//            } else { // 작성자가 아닐 때
//                binding.menuDecoratingImageView.isVisible = true
//                binding.menuReportButton.isVisible = true
//            }
//        }
//    }
//
//    // 메뉴의 삭제하기 버튼 클릭 함수
//    private fun clickMenuRemoveButton() {
//        binding.menuRemoveButton.setOnClickListener {
//            RetrofitManager.instance.getRemoveProduct(this, productId)
//            disappearsMenu() // 메뉴버튼 전체 사라짐
//            moveBack() // 이전화면 이동
//        }
//    }

//    // 메뉴의 수정하기 버튼 클릭 함수
//    private fun clickMenuModifyButton() {
//        binding.menuModifyButton.setOnClickListener {
//            val intent = Intent(this, AddProductsActivity::class.java).apply {
//                putExtra(PRODUCT_TITLE,productTitle)
//                putExtra(PRODUCT_BUY_PRICE,productBuyPrice)
//                putExtra(PRODUCT_BUY_COUNT,productBuyCount)
//                putExtra(PRODUCT_SHARE_PRICE,productSharePrice)
//                putExtra(PRODUCT_SHARE_COUNT,productShareCount)
//                putExtra(PRODUCT_BUY_YEAR,productBuyYear)
//                putExtra(PRODUCT_BUY_MONTH,productBuyMonth)
//                putExtra(PRODUCT_BUY_DAY,productBuyDay)
//                putExtra(PRODUCT_DESCRIPTION,productDescription)
//                putExtra(PRODUCT_LATITUDE, latitude)
//                putExtra(PRODUCT_LONGITUDE, longitude)
//                putExtra(PRODUCT_IMAGE, productImageArray)
//                putExtra(PRODUCT_RECEIPT_IMAGE,receiptUri)
//                putExtra(PRODUCT_TYPE, 1)
//                putExtra(PRODUCT_ID, productId)
//                putExtra(PRODUCT_CATEGORY_ID, categoryId)
//            }
//            startActivityForResult(intent,MOVE_MODIFY_CODE)
//        }
//    }

//    // 메뉴의 신고하기 버튼 클릭 함수
//    private fun clickMenuReportButton() {
//        binding.menuReportButton.setOnClickListener {
//            disappearsMenu() // 메뉴버튼 전체 사라짐
//        }
//    }

//    // 메뉴버튼 전체 사라짐
//    private fun disappearsMenu() {
//        binding.menuDecoratingImageView.isVisible = false
//        binding.menuRemoveButton.isVisible = false
//        binding.menuModifyButton.isVisible = false
//        binding.menuReportButton.isVisible = false
//    }

}