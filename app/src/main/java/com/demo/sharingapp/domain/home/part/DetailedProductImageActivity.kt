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

        val a = intent.getStringArrayExtra("data") ?: return
        // 상품 이미지
        val images = a
            .map { uriString -> DetailedImageData(uriString) }
        val frameAdapter = FrameAdapter(images){}
        binding.productImageViewPager.adapter = frameAdapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.productImageViewPager,
        ) { tab, position ->
            binding.productImageViewPager.currentItem = tab.position
        }.attach()

        binding.backButton.setOnClickListener {
            finish()
        }


    }
}