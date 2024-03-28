package com.demo.sharingapp.domain.home.other_profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentOtherProfileReviewBinding
import com.demo.sharingapp.domain.review.ReviewAdepter
import com.demo.sharingapp.retrofit.RetrofitManager

class ReviewFragment: Fragment(R.layout.fragment_other_profile_review) {
    private lateinit var binding: FragmentOtherProfileReviewBinding
    private lateinit var reviewAdepter: ReviewAdepter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtherProfileReviewBinding.bind(view)

        reviewAdepter = ReviewAdepter()

        binding.reviewRecyclerView.apply {
            adapter = reviewAdepter
            layoutManager = LinearLayoutManager(this@ReviewFragment.requireContext())
        }

        val receivedBundle = arguments
        if (receivedBundle != null) {
            val sellerId = receivedBundle.getLong("sellerId")

            if(sellerId != 0L){
                RetrofitManager.instance.getReview(this@ReviewFragment.requireContext(), userId = sellerId){
                    reviewAdepter.submitList(it)
                }
            }

        }

    }
}