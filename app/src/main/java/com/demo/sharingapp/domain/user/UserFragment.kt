package com.demo.sharingapp.domain.user

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.demo.sharingapp.MyApplication.Companion.mainViewModel
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentUserBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.chat.chatroom.RequestDialog
import com.demo.sharingapp.domain.user.review.ReviewActivity
import com.demo.sharingapp.domain.user.saveProductList.SaveProductListActivity
import com.demo.sharingapp.domain.user.shareProductList.ShareProductListActivity
import com.demo.sharingapp.domain.user.soldOutProduct.SoldOutProductActivity
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.login.logout.LogoutDialog
import com.demo.sharingapp.login.logout.LogoutDialogInterface
import com.demo.sharingapp.login.signout.DeleteAccountDialog
import com.demo.sharingapp.login.signout.DeleteAccountDialogInterface
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.REFRESH_TOKEN
import com.demo.sharingapp.utils.Constants.USER_ID
import com.kakao.sdk.user.UserApiClient
import ua.naiksoftware.stomp.StompClient
import kotlin.math.roundToInt

class UserFragment: Fragment(R.layout.fragment_user), DeleteAccountDialogInterface, LogoutDialogInterface {

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

        // 내 공유글 목록 클륵
        binding.soldOutListButton.setOnClickListener {
            Log.e("button","버튼 클릭 내공유글")
            val intent = Intent(context, SoldOutProductActivity::class.java)
            startActivity(intent)
        }

        // 후기 목록 클릭
        binding.reviewListButton.setOnClickListener {
            val intent = Intent(context, ReviewActivity::class.java)
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

        // 로그아웃 클릭
        binding.logoutButton.setOnClickListener {
            logoutDialog()
        }

        // 회원 탈퇴 클릭
        binding.deleteAccountButton.setOnClickListener {
            val userId = SharedPreferencesData.getLongData(this.requireContext(), USER_ID)
            deleteAccountDialog(userId)
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

    // 회원탈퇴 알림창 띄우기
    private fun logoutDialog() {
        val dialog =
            LogoutDialog(this)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.requireActivity().supportFragmentManager,
            "DeleteAccountDialog")
    }



    // 회원탈퇴 알림창 띄우기
    private fun deleteAccountDialog(
        userId: Long,
    ) {
        val dialog =
            DeleteAccountDialog(this,userId)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.requireActivity().supportFragmentManager,
            "DeleteAccountDialog")
    }

    // 다이얼로그에서 회원탈퇴버튼 클릭시
    override fun onDeleteAccountButtonClick(userId: Long) {

        RetrofitManager.instance.deleteAccount(this.requireContext(),userId){
            if (it == 0){
                SharedPreferencesData.removeAllData(this.requireContext())
                val intent = Intent(context, LoginView::class.java)
                startActivity(intent)
                this.requireActivity().finish()
                Toast.makeText(context, "정상적으로 탈퇴 처리가 되었습니다.", Toast.LENGTH_SHORT).show()
            }

        }


    }

    // 다이얼로그에서 로그아웃 클릭시
    override fun logoutButtonClick() {
        RetrofitManager.instance.postLogout(this.requireContext())
        SharedPreferencesData.removeAllData(this.requireContext())
        val intent = Intent(context, LoginView::class.java)
        startActivity(intent)
        this.requireActivity().finish()
    }

}