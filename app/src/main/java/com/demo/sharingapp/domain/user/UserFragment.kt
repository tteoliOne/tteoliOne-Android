package com.demo.sharingapp.domain.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.sharingapp.MyApplication.Companion.mainViewModel
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentUserBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.user.saveProductList.SaveProductListActivity
import com.demo.sharingapp.domain.user.shareProductList.ShareProductListActivity
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.REFRESH_TOKEN
import com.kakao.sdk.user.UserApiClient
import kotlin.math.roundToInt

class UserFragment: Fragment(R.layout.fragment_user) {

    private lateinit var binding: FragmentUserBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)

        var nickname = ""
        var description = ""
        var profileImage = ""
        RetrofitManager.instance.getMyInfo(this.requireContext()){
            binding.nicknameTextView.text = it.nickname
            nickname = it.nickname
            binding.goodCountTextView.text = ((it.thumbsUpScore*10).roundToInt()/10.0).toString()
            if(it.intro != null && it.intro != ""){
                binding.descriptionTextView.text = it.intro
                description = it.intro
            }
            Glide.with(binding.userImageView)
                .load(it.profile)
                .circleCrop()
                .into(binding.userImageView)
            profileImage = it.profile
        }

        // 내 공유글 목록 클륵
        binding.myShareListButton.setOnClickListener {
            Log.e("button","버튼 클릭 내공유글")
            val intent = Intent(context, ShareProductListActivity::class.java)
            startActivity(intent)
        }

        // 저장글 목록 클릭
        binding.saveProductListButton.setOnClickListener {
            Log.e("button","버튼 클릭 저장글 목록")
            val intent = Intent(context, SaveProductListActivity::class.java)
            startActivity(intent)
        }

        // 내 정보 수정 클릭
        binding.userSettingButton.setOnClickListener {
            val action = UserFragmentDirections.actionUserFragmentToUserSettingFragment(profileImage = profileImage,nickname = nickname, description = description)
            findNavController().navigate(action)
        }

        binding.logoutButton.setOnClickListener {
            //logout()
            SharedPreferencesData.removeAllData(this.requireContext())
            val intent = Intent(context, LoginView::class.java)
            startActivity(intent)
        }

        //초기 닉네임 입력 함수 호출
       // initNickname()
       // initId()


    }

    // 초기 닉네임 입력 함수
    private fun initNickname() {
//        mainViewModel = ViewModelProvider(this.requireActivity()).get(MainViewModel::class.java)
//        mainViewModel.currentUserInput.observe(this.requireActivity(), Observer {
//            binding.nicknameTextView.text = it.toString()
//        })

        binding.nicknameTextView.text = SharedPreferencesData.getData(this.requireContext(),
            Constants.NICKNAME)
    }

    // 초기 id 입력 함수
    private fun initId() {
        mainViewModel = ViewModelProvider(this.requireActivity()).get(MainViewModel::class.java)

    }

    // 카카오 로그아웃 함수
    private fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null){
                Log.e("kaokaoLogout", "로그아웃 실패 ,에러 : $error")
            }else{
                Log.e("kaokaoLogout", "로그아웃 성공 ")
                val intent = Intent(context, LoginView::class.java)
                startActivity(intent)
            }
        }
    }

}