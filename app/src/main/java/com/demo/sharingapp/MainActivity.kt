package com.demo.sharingapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.databinding.ActivityMainBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.home.HomeFragment
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.MOVE_DETAILED_CODE
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.REFRESH_TOKEN
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity(), HomeFragment.MyFragmentListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding:ActivityMainBinding


    private lateinit var saveProductAdapter: LikeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val latitude = intent.getDoubleExtra(Constants.LATITUDE,0.0)
        val longitude = intent.getDoubleExtra(LONGITUDE,0.0)

        // 찜 목록 닫기 버튼 클릭
        clickLikeListCloseButton()

        // 로그인 상태 확인 함수 호출
        checkHasLogin()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.updateMyPlace(longitude,latitude)

        // 바텀네비 초기 설정 함수 호출
        initNavigation()

        // 리사이클러뷰 초기 설정
        initRecyclerView()


        navHostFragment.navController.addOnDestinationChangedListener{ a,b,c ->
            Log.e("bb", " a = $a , b = ${b.id} , ${R.id.userFragment} , c = $c")
            if (b.id == R.id.userSettingFragment || b.id == R.id.homePartProductFragment){
                binding.bottomNavigationView.isVisible = false
            }else if (b.id == R.id.userFragment || b.id == R.id.homeFragment) {
                binding.bottomNavigationView.isVisible = true
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    binding.loadingView.isVisible = true

                    // 첫 번째 아이템이 클릭되었을 때의 처리
                    // 예: 네비게이션 뷰에서 특정 프래그먼트로 이동
                    navHostFragment.navController.navigate(R.id.homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chatFragment -> {
                    // 두 번째 아이템이 클릭되었을 때의 처리
                    // 예: 네비게이션 뷰에서 다른 프래그먼트로 이동
                    navHostFragment.navController.navigate(R.id.chatFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.userFragment -> {
                    // 두 번째 아이템이 클릭되었을 때의 처리
                    // 예: 네비게이션 뷰에서 다른 프래그먼트로 이동
                    navHostFragment.navController.navigate(R.id.userFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                else -> return@setOnNavigationItemSelectedListener false
            }
        }

    }

    // 찜 목록 닫기 버튼 클릭 함수
    private fun clickLikeListCloseButton() {
        binding.likeListCloseButton.setOnClickListener {
            binding.drawerView.closeDrawer(Gravity.LEFT)
            binding.navHostFragment.bringToFront()
        }
    }

    // 리사이클러뷰 초기 설정 함수
    private fun initRecyclerView() {
        saveProductAdapter = LikeListAdapter(){
            val intent = Intent(this,DetailedProductActivity::class.java)
                .putExtra(PRODUCT_ID,it)
            startActivityForResult(intent, MOVE_DETAILED_CODE)
        }
        binding.likeListRecyclerView.apply {
            adapter = saveProductAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    // 바텀네비 초기 설정 함수
    private fun initNavigation(){
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigationView
            .setupWithNavController(navHostFragment.navController)
    }


    // 해쉬 키 확인 함수
    private fun findKeyHash() {
        var keyHash = Utility.getKeyHash(this)
        Log.e("keyHash",keyHash)

    }

    // 로그인 상태 확인
    private fun checkHasLogin() {

        val checkIsRefreshToken = SharedPreferencesData.containsData(this, REFRESH_TOKEN)

        if (!checkIsRefreshToken){
            moveLogin()
        }

    }

    // 로그인 화면으로 이동 함수
    private fun moveLogin() {
        val intent = Intent(this, LoginView::class.java)
        startActivity(intent)
        finish()
    }

    // 쉐어드프리퍼런스에서 데이터 들고 오는 함수
    private fun getSharedData() {
        val accessToken = SharedPreferencesData.getData(this,ACCESS_TOKEN)
        val refreshToken = SharedPreferencesData.getData(this, REFRESH_TOKEN)
        val nickname = SharedPreferencesData.getData(this, NICKNAME)
        Log.e("getSharedData", "accessToken : $accessToken, refreshToken : $refreshToken, nickname : $nickname ")
    }


    // 홈프레그먼트에서 버튼 클릭시 동작 함수
    override fun onButtonClicked() {
        RetrofitManager.instance.getSaveProduct(this@MainActivity){
            it.let {
                saveProductAdapter.submitList(it.products)
            }

        }

        binding.drawerView.openDrawer(Gravity.LEFT)
        Log.e("aa","MyFragmentListener")
        binding.drawerView.bringToFront()
        binding.drawerView.setScrimColor(Color.TRANSPARENT)
    }

    override fun onLoading() {
        binding.loadingView.isVisible=false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MOVE_DETAILED_CODE && resultCode == RESULT_OK) {
            val intent = this.intent
            finish()
            startActivity(intent)

        }
    }

}