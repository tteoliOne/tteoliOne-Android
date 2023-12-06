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
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.LONGITUDE
import com.demo.sharingapp.utils.Constants.NICKNAME
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



        binding.navHostFragment

        binding.likeListCloseButton.setOnClickListener {
            binding.drawerView.closeDrawer(Gravity.LEFT)
            binding.navHostFragment.bringToFront()
        }


        // 로그인 상태 확인 함수 호출
        checkHasLogin()

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val latitude = intent.getDoubleExtra(Constants.LATITUDE,0.0)
        val longitude = intent.getDoubleExtra(LONGITUDE,0.0)
        mainViewModel.updateMyPlace(longitude,latitude)




        // 바텀네비 초기 설정 함수 호출
        initNavigation()

        initRecyclerView()


        navHostFragment.navController.addOnDestinationChangedListener{ a,b,c ->
            //Log.e("bb", " a = $a , b = ${b.id} , ${R.id.userFragment} , c = $c")
            if (b.id == R.id.userSettingFragment || b.id == R.id.homePartProductFragment){
                binding.bottomNavigationView.isVisible = false
            }else if (b.id == R.id.userFragment || b.id == R.id.homeFragment) {
                binding.bottomNavigationView.isVisible = true
            }
        }


        KakaoSdk.init(this, "b7724ccdfc3f8f5fef039b767bdd06d3")
        // 해쉬 키 확인 함수 호출
        //findKeyHash()




    }

    private fun initRecyclerView() {
        saveProductAdapter = LikeListAdapter()
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

        // 토큰을 가지고 있는지 확인
//        if (AuthApiClient.instance.hasToken()) {
//            UserApiClient.instance.accessTokenInfo { _, error ->
//
//                if (error != null) {
//                    //로그인 필요
//                    if (error is KakaoSdkError && error.isInvalidTokenError() == true) {
//
//                        // 로그인 화면으로 이동 함수 호출
//                        moveLogin()
//
//                    }
//                    else {
//                        //기타 에러
//                    }
//                }
//                else {
//                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
//                }
//            }
//        }
//        else { //로그인 필요
//
//            // 로그인 화면으로 이동 함수 호출
//            moveLogin()
//        }

    }

    // 로그인 화면으로 이동 함수
    private fun moveLogin() {
        val intent = Intent(this, LoginView::class.java)
        startActivity(intent)
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


}