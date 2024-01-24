package com.demo.sharingapp.domain.home.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSearchResultBinding

class SearchResultFragment: Fragment(R.layout.fragment_search_result) {
    private lateinit var binding: FragmentSearchResultBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchResultBinding.bind(view)

        val receivedBundle = arguments
        if (receivedBundle != null) {
            val data = receivedBundle.getString("key")
            // data를 사용하여 작업 수행
            binding.SearchTitleTextView.text = String.format("%s 검색 결과", data)
        }
    }
}