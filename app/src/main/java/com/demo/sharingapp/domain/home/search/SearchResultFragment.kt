package com.demo.sharingapp.domain.home.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSearchResultBinding
import com.demo.sharingapp.domain.home.HomePartProductAdepter
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class SearchResultFragment : Fragment(R.layout.fragment_search_result) {

    private lateinit var binding: FragmentSearchResultBinding

    private lateinit var homePartProductsAdepter: HomePartProductAdepter

    private var latitude = 0.0
    private var longitude = 0.0

    private var data = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchResultBinding.bind(view)

        val receivedBundle = arguments


        if (receivedBundle != null) {
            data = receivedBundle.getString("key") ?: return
        }

        val linearLayoutManager = LinearLayoutManager(this@SearchResultFragment.requireContext())
        homePartProductsAdepter = HomePartProductAdepter(onLikeClick = {
            RetrofitManager.instance.postProductLike(this.requireContext(), it)
        },
            onViewClick = {
                val intent = Intent(this@SearchResultFragment.requireContext(),
                    DetailedProductActivity::class.java)
                    .putExtra(Constants.PRODUCT_ID, it)
                startActivityForResult(intent, Constants.MOVE_DETAILED_CODE)
            }
        )
        binding.searchRecyclerView.apply {
            adapter = homePartProductsAdepter
            layoutManager = linearLayoutManager
        }

        // 내부저장소에 데이터가 있는지 확인
        if (checkSharedPreferencesData(Constants.LONGITUDE) && checkSharedPreferencesData(Constants.LATITUDE)) {
            longitude =
                SharedPreferencesData.getData(this.requireContext(), Constants.LONGITUDE).toDouble()
            latitude =
                SharedPreferencesData.getData(this.requireContext(), Constants.LATITUDE).toDouble()
        }

        RetrofitManager.instance.getSearch(this@SearchResultFragment.requireContext(),
            latitude = latitude,
            longitude = longitude,
            page = 0,
            q = data){
            homePartProductsAdepter.submitList(it.list.content)
            binding.SearchTitleTextView.text = String.format("%s 검색 결과", it.q)
        }
    }

    // 내부저장소에 데이터가 있는지 확인 함수
    private fun checkSharedPreferencesData(dataName: String): Boolean {
        return SharedPreferencesData.containsData(this.requireContext(), dataName)
    }
}