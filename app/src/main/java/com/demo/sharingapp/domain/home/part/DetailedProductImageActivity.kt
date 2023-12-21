package com.demo.sharingapp.domain.home.part

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.demo.sharingapp.databinding.ActivityDetailedProductImageBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData
import com.google.android.material.tabs.TabLayoutMediator

class DetailedProductImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedProductImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageArray = intent.getStringArrayExtra("data") ?: return
        // 상품 이미지
        val images = imageArray
            .map { uriString -> DetailedImageData(uriString) }

        // 프레임어뎁터 설정
       val frameAdapter = DetailedFrameAdapter(images)
        binding.productImageViewPager.adapter = frameAdapter

        // 테이블 레이아웃 설정
        TabLayoutMediator(
            binding.tabLayout,
            binding.productImageViewPager,
        ) { tab, position ->
            binding.productImageViewPager.currentItem = tab.position
        }.attach()

        // 이전 버튼 클릭
        binding.backButton.setOnClickListener {
            finish()
        }


    }
}