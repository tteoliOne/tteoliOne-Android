package com.demo.sharingapp.domain.home.other_profile

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityOtherProfileBinding
import com.demo.sharingapp.databinding.ActivitySearchBinding
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.SELLER_ID
import kotlin.math.roundToInt

class OtherProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtherProfileBinding
    private lateinit var navHostOtherProfileFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 판매자 id
        val sellerId = intent.getLongExtra(SELLER_ID, 0L)

        // 공유 위치
        var longitude = 0.0
        var latitude = 0.0
        // 내부저장소에 데이터가 있는지 확인
        if (checkSharedPreferencesData(Constants.LONGITUDE) && checkSharedPreferencesData(
                Constants.LATITUDE)
        ) {
            longitude =
                SharedPreferencesData.getData(this, Constants.LONGITUDE).toDouble()
            latitude =
                SharedPreferencesData.getData(this, Constants.LATITUDE).toDouble()
        }

        val bundle = Bundle()
        bundle.putLong("sellerId", sellerId)
        bundle.putDouble("longitude", longitude)
        bundle.putDouble("latitude", latitude)

        // 상대방 프로필 정보 가져오기(상단바)
        getOtherProfile(sellerId)

        // 프로그먼트 초기 설정
        initFragment(bundle)

        // 카테고리 설정 (판매중, 판매완료, 후기)
        initCategory(bundle)

        binding.backButton.setOnClickListener {
            finish()
        }


    }

    // 상대방 프로필 정보 가져오기(상단바) 함수
    private fun getOtherProfile(sellerId: Long) {
        RetrofitManager.instance.getOtherProfileSimple(this, sellerId) {
            // 상대방 닉네임
            val nickname = it.nickname + "님 가게"
            val spannable = SpannableStringBuilder(nickname)
            val targetText = "님 가게"
            val start = nickname.indexOf(targetText)
            val end = start + targetText.length
            val color = ForegroundColorSpan(Color.BLACK)
            spannable.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.nicknameTextView.text = spannable

            // 프로필
            Glide.with(binding.profileImageView)
                .load(it.profile)
                .circleCrop()
                .into(binding.profileImageView)

            // 따봉 점수
            binding.goodCountTextView.text = ((it.ddabongScore * 10).roundToInt() / 10.0).toString()

            // 소개글
            if (it.intro != null) {
                if (it.intro.isNotEmpty()) {
                    binding.introTextView.text = it.intro
                }
            }

            // 판매 중 개수
            binding.onSellTextView.text = getString(R.string.on_sell_count, it.newProductCount)

            // 판매 완료 개수
            binding.soldOutTextView.text =
                getString(R.string.sold_out_product_count, it.soldOutProductCount)

            // 후기 개수
            binding.reviewTextView.text = getString(R.string.review_count, it.reviewCount)

        }
    }

    // 프래그먼트 초기 함수
    private fun initFragment(bundle: Bundle) {
        navHostOtherProfileFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_other_profile_fragment) as NavHostFragment

        navHostOtherProfileFragment.navController.navigate(R.id.onSellFragment, bundle)
    }

    // 카테고리 설정 (판매중, 판매완료, 후기) 함수
    private fun initCategory(bundle: Bundle) {
        // 카테고리 선택 시 색상
        val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))

        // 판매 중 클릭
        clickOnSell(bundle, newTintColor)

        // 판매 완료 클릭
        clickSoldOut(bundle, newTintColor)

        // 후기 클릭
        clickReview(bundle, newTintColor)
    }

    // 판매 중 클릭 함수
    private fun clickOnSell(
        bundle: Bundle,
        newTintColor: ColorStateList,
    ) {
        binding.onSellLayout.setOnClickListener {
            navHostOtherProfileFragment.navController.navigate(R.id.onSellFragment, bundle)
            categoryAllInit() // 카테고리 초기화
            binding.onSellLayout.backgroundTintList = newTintColor
            binding.onSellTextView.setTextColor(Color.WHITE)
        }
    }

    // 판매 완료 클릭 함수
    private fun clickSoldOut(
        bundle: Bundle,
        newTintColor: ColorStateList,
    ) {
        binding.soldOutLayout.setOnClickListener {
            navHostOtherProfileFragment.navController.navigate(R.id.salesCompleteFragment, bundle)
            categoryAllInit() // 카테고리 초기화
            binding.soldOutLayout.backgroundTintList = newTintColor
            binding.soldOutTextView.setTextColor(Color.WHITE)

        }
    }

    // 후기 클릭 함수
    private fun clickReview(
        bundle: Bundle,
        newTintColor: ColorStateList,
    ) {
        binding.reviewLayout.setOnClickListener {
            navHostOtherProfileFragment.navController.navigate(R.id.reviewFragment, bundle)
            categoryAllInit() // 카테고리 초기화
            binding.reviewLayout.backgroundTintList = newTintColor
            binding.reviewTextView.setTextColor(Color.WHITE)
        }
    }

    // 카테고리 초기화 함수
    private fun categoryAllInit() {
        val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.white, theme))
        binding.soldOutLayout.backgroundTintList = newTintColor
        binding.onSellLayout.backgroundTintList = newTintColor
        binding.reviewLayout.backgroundTintList = newTintColor
        binding.soldOutTextView.setTextColor(Color.BLACK)
        binding.onSellTextView.setTextColor(Color.BLACK)
        binding.soldOutTextView.setTextColor(Color.BLACK)
        binding.reviewTextView.setTextColor(Color.BLACK)

    }

    // 내부저장소에 데이터가 있는지 확인 함수
    private fun checkSharedPreferencesData(dataName: String): Boolean {
        return SharedPreferencesData.containsData(this, dataName)
    }

}