package com.demo.sharingapp.domain.user.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityReviewBinding
import com.demo.sharingapp.domain.review.ReviewAdepter
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.USER_ID

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var reviewAdepter: ReviewAdepter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reviewAdepter = ReviewAdepter()

        binding.reviewRecyclerView.apply {
            adapter = reviewAdepter
            layoutManager = LinearLayoutManager(this@ReviewActivity)
        }


        val userId = SharedPreferencesData.getLongData(this,USER_ID)

        if (userId != 0L) {
            RetrofitManager.instance.getReview(this@ReviewActivity, userId = userId) {
                reviewAdepter.submitList(it)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }


    }
}