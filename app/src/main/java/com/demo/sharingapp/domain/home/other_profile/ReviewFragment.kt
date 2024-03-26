package com.demo.sharingapp.domain.home.other_profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentOtherProfileReviewBinding

class ReviewFragment: Fragment(R.layout.fragment_other_profile_review) {
    private lateinit var binding: FragmentOtherProfileReviewBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtherProfileReviewBinding.bind(view)

    }
}