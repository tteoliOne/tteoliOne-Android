package com.demo.sharingapp.domain.other_profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSalesCompleteBinding

class SalesCompleteFragment: Fragment(R.layout.fragment_sales_complete) {
    private lateinit var binding: FragmentSalesCompleteBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSalesCompleteBinding.bind(view)

    }
}