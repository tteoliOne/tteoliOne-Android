package com.demo.sharingapp.domain.home.part

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.sharingapp.databinding.ActivityDetailedProductImageBinding

class DetailedProductImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailedProductImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedProductImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }


    }
}