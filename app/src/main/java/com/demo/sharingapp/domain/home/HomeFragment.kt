package com.demo.sharingapp.domain.home

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.AddProductsActivity
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentHomeBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.MOVE_DETAILED_CODE
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.USER_ID


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var homeVegetableAdepter: HomeAdepter
    private lateinit var homeFruitsAdepter: HomeAdepter
    private lateinit var homeFastFoodAdepter: HomeAdepter
    private lateinit var homeMeatAdepter: HomeAdepter
    private lateinit var homeSeafoodAdepter: HomeAdepter
    private lateinit var homeEtcAdepter: HomeAdepter


    private var longitude = 0.0
    private var latitude = 0.0

    //
    private var listener: MyFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MyFragmentListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement MyFragmentListener")
        }
    }



    private lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // 초기 nickname 설정 함수 호출
        initNickname()

//        val longitude = mainViewModel.longitude.value
//       val latitude = mainViewModel.latitude.value

        if (checkSharedPreferencesData(LONGITUDE) && checkSharedPreferencesData(LATITUDE)){
            longitude = SharedPreferencesData.getData(this.requireContext(), LONGITUDE).toDouble()
            latitude = SharedPreferencesData.getData(this.requireContext(), LATITUDE).toDouble()
        }
        val accessToken = SharedPreferencesData.getData(this.requireContext(),ACCESS_TOKEN)
        val userId = SharedPreferencesData.getLongData(this.requireContext(),USER_ID)

        Log.e("aa", mainViewModel.longitude.value.toString())

        // 초기 전체 리사이클러 뷰 설정 함수 호출
        initAllRecyclerView(accessToken)

        initProduct(accessToken, longitude, latitude, userId)

        // 상품 등록 버튼 클릭 시
        addProductButton()

        clickNicknameButton()

        binding.homeScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == 0){
                binding.homeAddButton.extend()
            }else{
                binding.homeAddButton.shrink()
            }
        }

        //
        binding.vegetableTextView.setOnClickListener {
            val intent = Intent(this.requireActivity(),DetailedProductActivity::class.java)
            startActivity(intent)
        }

    }

    private fun clickNicknameButton() {
        binding.topBar.nicknameSettingButton.setOnClickListener {

            val popup = PopupMenu(this@HomeFragment.requireContext(), it)
            popup.menuInflater.inflate(R.menu.menu_like_list, popup.menu)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.showLikeListMenu -> {
                        (activity as? MyFragmentListener)?.onButtonClicked()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.logoutMenu -> {

                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popup.show()
        }
    }

    private fun initProduct(
        accessToken: String,
        longitude: Double?,
        latitude: Double?,
        userId: Long,
    ) {
        if (accessToken != "") {
            // 서버에서 상품 데이터 불러오기
            getProducts(longitude, latitude, accessToken, userId)
        }
    }

    // 초기 전체 리사이클러 뷰 설정 함수
    private fun initAllRecyclerView(accessToken: String) {
        // 야채 어댑터 설정
        homeVegetableAdepter = homeAdepter(accessToken, "채소")
        // 과일 어댑터 설정
        homeFruitsAdepter = homeAdepter(accessToken, "과일")
        // 간편식 어댑터 설정
        homeFastFoodAdepter = homeAdepter(accessToken, "간편식")
        // 정육 어댑터 설정
        homeMeatAdepter = homeAdepter(accessToken, "정육")
        // 수산물 어댑터 설정
        homeSeafoodAdepter = homeAdepter(accessToken, "수산물")
        // 기타 어댑터 설정
        homeEtcAdepter = homeAdepter(accessToken, "기타")

        initRecyclerView(homeVegetableAdepter, binding.vegetableRecyclerView)
        initRecyclerView(homeFruitsAdepter, binding.fruitsRecyclerView)
        initRecyclerView(homeFastFoodAdepter, binding.festFoodRecyclerView)
        initRecyclerView(homeMeatAdepter, binding.meatRecyclerView)
        initRecyclerView(homeSeafoodAdepter, binding.seafoodRecyclerView)
        initRecyclerView(homeEtcAdepter, binding.etcRecyclerView)
    }

    // homeAdepter 초기화 함수
    private fun homeAdepter(accessToken: String, category: String) = HomeAdepter(
        onMoreClick = {
            // 더보기 화면으로 이동 함수 호출
            movePartProductFragment(it, category)
        },
        onLikeClick = {
            // 좋아요 클릭 시 함수 호출
            likeClick(it, accessToken)
        },
        onViewClick = {
            val intent = Intent(this@HomeFragment.requireActivity(),DetailedProductActivity::class.java).putExtra(PRODUCT_ID,it)
            startActivityForResult(intent,MOVE_DETAILED_CODE)
        }
    )

    // 좋아요 클릭 시 함수
    private fun likeClick(it: Long, accessToken: String) {
        RetrofitManager.instance.postProductLike(this.requireContext(), it, accessToken)
    }

    // 더보기 화면으로 이동 함수
    private fun movePartProductFragment(data : List<ProductsData>, category: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToHomePartProductFragment(data.toTypedArray(),category)
        findNavController().navigate(action)
    }

    // 각 리사이클러뷰 초기 설정 함수
    private fun initRecyclerView(adepter: HomeAdepter, recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = adepter
            layoutManager = LinearLayoutManager(this@HomeFragment.requireContext()).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
    }

    // 데이터 받는 함수
    private fun getProducts(
        longitude: Double?,
        latitude: Double?,
        accessToken: String,
        userId: Long,
    ) {
        if (longitude != null && latitude != null) {
            RetrofitManager.instance.getProduct(this.requireContext(),
                longitude,
                latitude,
                accessToken,
                userId) {

                val productData = it.groupBy {
                    it.categoryId
                }

                // 채소 리사이클러뷰에 데이터 추가
                productData[1]?.forEach {
                    homeVegetableAdepter.submitList(it.products)
                }

                // 과일 리사이클러뷰에 데이터 추가
                productData[2]?.forEach {
                    homeFruitsAdepter.submitList(it.products)
                }

                // 간편식 리사이클러뷰에 데이터 추가
                productData[3]?.forEach {
                    homeFastFoodAdepter.submitList(it.products)
                }

                // 정육 리사이클러뷰에 데이터 추가
                productData[4]?.forEach {
                    homeMeatAdepter.submitList(it.products)
                }

                // 수산물 리사이클러뷰에 데이터 추가
                productData[5]?.forEach {
                    homeSeafoodAdepter.submitList(it.products)
                }

                // 기타 리사이클러뷰에 데이터 추가
                productData[6]?.forEach {
                    homeEtcAdepter.submitList(it.products)
                }


                Log.e("mao", productData[1].toString())
                Log.e("mao", productData[2].toString())
                Log.e("mao", productData[3].toString())
                Log.e("mao", productData[4].toString())
                Log.e("mao", productData[5].toString())
            }
        }
    }

    // 상품 등록 버튼 클릭 함수
    private fun addProductButton() {
        binding.homeAddButton.setOnClickListener {
            startActivity(Intent(this.requireActivity(), AddProductsActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MOVE_DETAILED_CODE && resultCode == RESULT_OK) {
            val intent = this@HomeFragment.requireActivity().intent
            this@HomeFragment.requireActivity().finish()
            startActivity(intent)
        }
    }

    // 초기 nickname 설정 함수
    private fun initNickname() {
        // 뷰 모델 프로바이더를 통해 뷰모델 가져오기
        // 라이프사이클을 가지고 있는 녀석을 넣어줌 즉 자기 자신
        // 우리가 가져오고 싶은 뷰모델 클레스를 넣어서 뷰모델을 가져오기
        mainViewModel = ViewModelProvider(this.requireActivity())[MainViewModel::class.java]
//        mainViewModel.currentUserInput.observe(this.requireActivity()) {
//            Log.e("TAG", "MainActivity - myNumberViewModel - currentValue 라이브 데이터 값 변경 : $it")
//            binding.topBar.nicknameTextView.text = it.toString()
//        }
        binding.topBar.nicknameTextView.text = SharedPreferencesData.getData(this.requireContext(),NICKNAME)

    }

    private fun checkSharedPreferencesData(dataName: String): Boolean{
        return SharedPreferencesData.containsData(this.requireContext(),dataName)
    }

    interface MyFragmentListener {
        fun onButtonClicked()
    }





}