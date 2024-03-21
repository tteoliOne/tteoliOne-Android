package com.demo.sharingapp.domain.home.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySearchBinding
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.ViewUtil.hideKeyboard
import kotlinx.coroutines.delay

class SearchActivity : AppCompatActivity(),SearchInitInterface {

    private lateinit var navHostSearchFragment: NavHostFragment
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchInputAdepter: SearchInputAdepter
    // 핸들러
    private val handler = Handler(Looper.getMainLooper())

    private var inputType = 0
    private var input:String? =null
    private var getSuccess = false

    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostSearchFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_search_fragment) as NavHostFragment

        searchInputAdepter = SearchInputAdepter(){
            binding.searchEditText.setText(it)

            // 검색결과 화면 프래그먼트로 이동
            moveSearchResult()
        }

        binding.searchInputRecyclerView.apply {
            adapter = searchInputAdepter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        // 이전 버튼 클릭
        clickBackButton()

        // 키보드에 검색 버튼 클릭
        clickKeyboardEnter()

        // 키보드 변경 이벤트
        changeKeyboardValue()

        binding.editTextAllRemove.setOnClickListener {
            binding.searchEditText.setText("")
        }

    }

    // 키보드 변경 이벤트 함수
    private fun changeKeyboardValue() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s.toString().trim().isEmpty()) { // 검색창이 비어 있을때
                    val bundle = Bundle()
                    if (input != null) {
                        bundle.putString("key", input)
                        Log.e("erdsf", "메인$input")
                        input = null
                    }
                    navHostSearchFragment.navController.navigate(R.id.searchInitFragment, bundle)
                    binding.searchInputRecyclerView.isVisible = false
                    binding.navHostSearchFragment.isVisible = true

                } else if (inputType == 0) { // 검색창에 텍스트가 있을때

                    binding.searchInputRecyclerView.isVisible = true
                    binding.navHostSearchFragment.isVisible = false


                    val runnable = Runnable {
                        // 검색 데이터 정보 가져오기
                        getSearchData()
                    }
                    handler.removeCallbacks(runnable)
                    handler.postDelayed(
                        runnable,
                        500
                    )

                } else {
                    inputType = 0
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // 키보드에 검색 버튼 클릭 함수
    private fun clickKeyboardEnter() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && getSuccess) {

                // 검색결과 화면 프래그먼트로 이동
                moveSearchResult()
                true
            } else if (!getSuccess) {
                Toast.makeText(this, "검색결과가 없습니다", Toast.LENGTH_SHORT).show()
                true
            } else {
                false
            }
        }
    }

    // 이전 버튼 클릭 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // 검색결과 화면 프래그먼트로 이동 함수
    private fun moveSearchResult() {
        binding.searchEditText.hideKeyboard()

        input = binding.searchEditText.text.toString()

        inputType = 1

        binding.searchInputRecyclerView.isVisible = false
        binding.navHostSearchFragment.isVisible = true
        val data = binding.searchEditText.text.toString()
        val bundle = Bundle()
        bundle.putString("key", data)
        navHostSearchFragment.navController.navigate(R.id.searchResultFragment, bundle)
    }

    // 검색 데이터 정보 가져오기 함수
    private fun getSearchData() {
        // 내부저장소에 데이터가 있는지 확인
        if (checkSharedPreferencesData(Constants.LONGITUDE) && checkSharedPreferencesData(
                Constants.LATITUDE)
        ) {
            longitude =
                SharedPreferencesData.getData(this@SearchActivity, Constants.LONGITUDE).toDouble()
            latitude =
                SharedPreferencesData.getData(this@SearchActivity, Constants.LATITUDE).toDouble()
        }
        val data = binding.searchEditText.text.toString()
        RetrofitManager.instance.getSearch(this@SearchActivity,
            longitude = longitude,
            latitude = latitude,
            page = 0,
            q = data) {
            searchInputAdepter.submitList(it.list.content)
            getSuccess = it.list.numberOfElements >0
        }
    }

    // 내부저장소에 데이터가 있는지 확인 함수
    private fun checkSharedPreferencesData(dataName: String): Boolean {
        return SharedPreferencesData.containsData(this, dataName)
    }

    override fun rememberData(data: String) {
        binding.searchEditText.setText(data)
        moveSearchResult()

    }


}