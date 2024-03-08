package com.demo.sharingapp.domain.other_profile

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityOtherProfileBinding
import com.demo.sharingapp.databinding.ActivitySearchBinding

class OtherProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOtherProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(binding.profileImageView)
            .load("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/2f6c6cb5-d024-459a-ab2c-bb75ca292b09.jpeg")
            .circleCrop()
            .into(binding.profileImageView)

        val nickname = "딜리트딜리트님 가게"
        val spannable = SpannableStringBuilder(nickname)
        val targetText = "님 가게"
        val start = nickname.indexOf(targetText)
        val end = start + targetText.length

        val color = ForegroundColorSpan(Color.BLACK)
        spannable.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.nicknameTextView.text = spannable


        binding.salesCompleteLayout.setOnClickListener {
            Log.e("bb","bb")
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.salesCompleteLayout.backgroundTintList = newTintColor
            binding.salesCompleteTextView.setTextColor(Color.WHITE)
        }

        binding.onSellLayout.setOnClickListener {
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.onSellLayout.backgroundTintList = newTintColor
            binding.onSellTextView.setTextColor(Color.WHITE)
        }

        binding.reviewLayout.setOnClickListener {
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.reviewLayout.backgroundTintList = newTintColor
            binding.reviewTextView.setTextColor(Color.WHITE)
        }
    }

    private fun categoryAllInit() {
        val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.white, theme))
        binding.salesCompleteLayout.backgroundTintList = newTintColor
        binding.onSellLayout.backgroundTintList = newTintColor
        binding.reviewLayout.backgroundTintList = newTintColor
        binding.salesCompleteTextView.setTextColor(Color.BLACK)
        binding.onSellTextView.setTextColor(Color.BLACK)
        binding.salesCompleteTextView.setTextColor(Color.BLACK)
        binding.reviewTextView.setTextColor(Color.BLACK)

    }

}