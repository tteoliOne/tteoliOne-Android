package com.demo.sharingapp.domain.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentHomePartProductBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class HomePartProductFragment : Fragment(R.layout.fragment_home_part_product) {

    private val args: HomePartProductFragmentArgs by navArgs()

    private lateinit var mainViewModel: MainViewModel

    private lateinit var homePartProductsAdepter: HomePartProductAdepter

    private lateinit var binding: FragmentHomePartProductBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomePartProductBinding.bind(view)

        val accessToken = SharedPreferencesData.getData(this.requireContext(), Constants.ACCESS_TOKEN)
        val productsData: List<ProductsData> = args.data.toList()
        val category = args.name

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

        // 초기 리사이클러뷰 설정 함수 호출
        initRecyclerView(accessToken)

        binding.categoryTextView.text = category


        // 카테고리에 맞는 상품 데이터 받는 함수 호출
        getProducts(category)

        // 전 화면에 있는 데이터 넣기
        //homePartProductsAdepter.submitList(productsData)

    }

    // 초기 리사이클러뷰 설정 함수
    private fun initRecyclerView(accessToken: String) {
        homePartProductsAdepter = HomePartProductAdepter() {
            RetrofitManager.instance.postProductLike(this.requireContext(), it, accessToken)
        }
        binding.partProductRecyclerView.apply {
            adapter = homePartProductsAdepter
            layoutManager = LinearLayoutManager(this@HomePartProductFragment.requireContext())
        }
    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // 카테고리에 맞는 상품 데이터 받는 함수
    private fun getProducts(category: String) {

        val id = when(category){
            "채소" -> 1
            "과일" -> 2
            "간편식" -> 3
            "정육" -> 4
            "수산물" -> 5
            "기타" -> 6
            else -> 0
        }

        mainViewModel = ViewModelProvider(this.requireActivity())[MainViewModel::class.java]
        val longitude = mainViewModel.longitude.value
        val latitude = mainViewModel.latitude.value
        val accessToken = SharedPreferencesData.getData(this.requireContext(),
            Constants.ACCESS_TOKEN)
        val userId = SharedPreferencesData.getLongData(this.requireContext(), Constants.USER_ID)
        if (longitude != null && latitude != null) {
            RetrofitManager.instance.getProduct(this.requireContext(),
                longitude,
                latitude,
                accessToken,
                userId) {
                val productData = it.groupBy {
                    it.categoryId
                }

                productData[id]?.forEach {
                    homePartProductsAdepter.submitList(it.products)
                }

            }
        }
    }
}