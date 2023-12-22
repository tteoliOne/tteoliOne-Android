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
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentHomePartProductBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.login.signup.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.MOVE_DETAILED_CODE
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import java.time.LocalDate

class HomePartProductFragment : Fragment(R.layout.fragment_home_part_product) {

    private val args: HomePartProductFragmentArgs by navArgs()

    private lateinit var mainViewModel: MainViewModel

    private lateinit var homePartProductsAdepter: HomePartProductAdepter

    private lateinit var binding: FragmentHomePartProductBinding

    private var category = ""
    private var longitude = 0.0
    private var latitude = 0.0
    private var page = 0
    private var last = false

    private var sort = "createAt-asc"
    private var searchStartDate: LocalDate? = null
    private var searchEndDate: LocalDate? = null
    private var dateType = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("page", "시작")

        category = args.name
        longitude = args.longitude.toDouble()
        latitude = args.latitude.toDouble()
        binding = FragmentHomePartProductBinding.bind(view)

        val linearLayoutManager = LinearLayoutManager(this@HomePartProductFragment.requireContext())

        val accessToken =
            SharedPreferencesData.getData(this.requireContext(), Constants.ACCESS_TOKEN)


        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

        // 초기 리사이클러뷰 설정 함수 호출
        initRecyclerView(accessToken, linearLayoutManager)

        binding.categoryTextView.text = category


        // 카테고리에 맞는 상품 데이터 받는 함수 호출
        getProducts(category, page, longitude, latitude, searchStartDate, searchEndDate, sort)

        binding.partProductRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalCount = linearLayoutManager.itemCount
                val lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition >= (totalCount - 1) && !last && lastVisiblePosition > 28) {
                    Log.e("page", "aa $lastVisiblePosition , ${totalCount - 1}, $last")
                    page += 1
                    getProducts(category,
                        page,
                        longitude,
                        latitude,
                        searchStartDate,
                        searchEndDate,
                        sort)
                }
            }
        })

        binding.filterButton.setOnClickListener {
            showDialog()
        }

        // 전 화면에 있는 데이터 넣기
        //homePartProductsAdepter.submitList(productsData)

    }

    // 초기 리사이클러뷰 설정 함수
    private fun initRecyclerView(accessToken: String, linearLayoutManager: LinearLayoutManager) {
        homePartProductsAdepter = HomePartProductAdepter(onLikeClick = {
            RetrofitManager.instance.postProductLike(this.requireContext(), it, accessToken)
        },
            onViewClick = {
                val intent = Intent(this@HomePartProductFragment.requireContext(),
                    DetailedProductActivity::class.java)
                    .putExtra(PRODUCT_ID, it)
                startActivityForResult(intent, MOVE_DETAILED_CODE)
            }
        )
        binding.partProductRecyclerView.apply {
            adapter = homePartProductsAdepter
            layoutManager = linearLayoutManager
        }
    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // 카테고리에 맞는 상품 데이터 받는 함수
    private fun getProducts(
        category: String,
        page: Int,
        longitude: Double,
        latitude: Double,
        searchStartDate: LocalDate?,
        searchEndDate: LocalDate?,
        sort: String? = "asc",
    ) {
        val id = when (category) {
            "채소" -> 1
            "과일" -> 2
            "간편식" -> 3
            "정육" -> 4
            "수산물" -> 5
            "기타" -> 6
            else -> 0
        }

        var startDate: String? = null
        var endDate: String? = null
        if (searchStartDate != null && searchEndDate != null) {
             startDate = String.format("%d%02d%02d",
                searchStartDate.year,
                searchStartDate.monthValue,
                searchStartDate.dayOfMonth)

             endDate = String.format("%d%02d%02d",
                searchEndDate.year,
                searchEndDate.monthValue,
                searchEndDate.dayOfMonth)
        }



        mainViewModel = ViewModelProvider(this.requireActivity())[MainViewModel::class.java]
        val accessToken = SharedPreferencesData.getData(this.requireContext(),
            Constants.ACCESS_TOKEN)
        if (longitude != null && latitude != null) {
            RetrofitManager.instance.getPartProduct(this.requireContext(),
                longitude,
                latitude,
                accessToken,
                categoryId = id.toLong(),
                page = page,
                sort = sort,
                searchStartDate = startDate,
                searchEndDate = endDate
            ) {
                if (page > 0) {
                    this.last = it.last
                    homePartProductsAdepter.submitList(homePartProductsAdepter.currentList + it.content.orEmpty())
                } else {
                    homePartProductsAdepter.submitList(it.content) {
                        binding.partProductRecyclerView.scrollToPosition(0)
                    }

                }
            }
        }

    }

    // 알림창 띄우기
    private fun showDialog() {
        val dialog = HomePartProductDialog(sort,
            searchStartDate,
            searchEndDate,
            dateType) { searchStartDate, searchEndDate, sort, dateType ->
            this.searchStartDate = searchStartDate
            this.searchEndDate = searchEndDate
            this.sort = sort
            this.dateType = dateType
            getProducts(category = category,
                page = 0,
                longitude = longitude,
                latitude = latitude,
                searchStartDate = searchStartDate,
                searchEndDate = searchEndDate,
                sort = sort
            )
        }


        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@HomePartProductFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MOVE_DETAILED_CODE && resultCode == Activity.RESULT_OK) {
            getProducts(category = category,
                page = 0,
                longitude = longitude,
                latitude = latitude, searchStartDate, searchEndDate, sort)
        }
    }
}