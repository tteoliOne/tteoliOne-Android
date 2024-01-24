package com.demo.sharingapp.domain.home.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSearchInitBinding

class SearchInitFragment: Fragment(R.layout.fragment_search_init) {
    private lateinit var binding: FragmentSearchInitBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchInitBinding.bind(view)


    }
}