package com.demo.sharingapp.domain.home

import android.app.Activity
import android.content.Intent
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
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.MOVE_DETAILED_CODE
import com.demo.sharingapp.utils.Constants.PRODUCT_ID

class HomePartProductFragment : Fragment(R.layout.fragment_home_part_product) {

    private val args: HomePartProductFragmentArgs by navArgs()

    private lateinit var mainViewModel: MainViewModel

    private lateinit var homePartProductsAdepter: HomePartProductAdepter

    private lateinit var binding: FragmentHomePartProductBinding

    private var category = ""
    private var longitude = 0.0
    private var latitude = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category = args.name
        longitude = args.longitude.toDouble()
        latitude = args.latitude.toDouble()

        binding = FragmentHomePartProductBinding.bind(view)

        val accessToken = SharedPreferencesData.getData(this.requireContext(), Constants.ACCESS_TOKEN)


        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

        // 초기 리사이클러뷰 설정 함수 호출
        initRecyclerView(accessToken)

        binding.categoryTextView.text = category


        // 카테고리에 맞는 상품 데이터 받는 함수 호출
        getProducts(category,longitude,latitude)

        // 전 화면에 있는 데이터 넣기
        //homePartProductsAdepter.submitList(productsData)

    }

    // 초기 리사이클러뷰 설정 함수
    private fun initRecyclerView(accessToken: String) {
        homePartProductsAdepter = HomePartProductAdepter(onLikeClick = {
            RetrofitManager.instance.postProductLike(this.requireContext(), it, accessToken)
        },
        onViewClick = {
            val intent = Intent(this@HomePartProductFragment.requireContext(),DetailedProductActivity::class.java)
                .putExtra(PRODUCT_ID,it)
            startActivityForResult(intent, MOVE_DETAILED_CODE)
        }
            )
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
    private fun getProducts(category: String, longitude: Double, latitude: Double) {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MOVE_DETAILED_CODE && resultCode == Activity.RESULT_OK) {
            getProducts(category = category,longitude,latitude )
        }
    }
}