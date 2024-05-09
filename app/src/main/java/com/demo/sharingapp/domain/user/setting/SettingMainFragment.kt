package com.demo.sharingapp.domain.user.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSettingMainBinding
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.login.find_password.FindPasswordActivity
import com.demo.sharingapp.login.logout.LogoutDialog
import com.demo.sharingapp.login.logout.LogoutDialogInterface
import com.demo.sharingapp.login.signout.DeleteAccountDialog
import com.demo.sharingapp.login.signout.DeleteAccountDialogInterface
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.LOGIN_TYPE
import com.demo.sharingapp.utils.Constants.NOTIFY_STATE
import com.google.firebase.messaging.FirebaseMessaging

class SettingMainFragment: Fragment(R.layout.fragment_setting_main), LogoutDialogInterface ,
    DeleteAccountDialogInterface {

    private lateinit var binding: FragmentSettingMainBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingMainBinding.bind(view)

        // 로그인 타입에 따라 비밀번호 변경 클릭 여부
        val loginType = SharedPreferencesData.getIntData(this.requireContext(), LOGIN_TYPE)

        // 알림 설정 여부
        var notify = SharedPreferencesData.getBooleanData(this.requireContext(), NOTIFY_STATE)
        binding.switchButton.isChecked = notify


        // 이전 버튼 클릭
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 프로필 변경 클릭
        binding.profileSettingButton.setOnClickListener {
            val action = SettingMainFragmentDirections.actionSettingMainFragmentToSettingProfileFragment()
            findNavController().navigate(action)
        }

        // 비밀번호 변경 클릭
        binding.passwordSettingButton.setOnClickListener {
            Log.e("dafsas",loginType.toString())
            if (loginType == 0){
                val action= SettingMainFragmentDirections.actionSettingMainFragmentToSettingPasswordFragment()
                findNavController().navigate(action)
            }else{
                Toast.makeText(this.requireContext(),"소셜 로그인된 상태에서는 비밀번호를 변경할 수 없습니다.",Toast.LENGTH_SHORT).show()
            }

        }

        // 장소 변경 클릭
        binding.placeSettingButton.setOnClickListener {
            val action = SettingMainFragmentDirections.actionSettingMainFragmentToSettingPlaceFragment()
            findNavController().navigate(action)
        }

        // 알림 변경 클릭
        binding.switchButton.setOnClickListener {
            notify = !notify
            SharedPreferencesData.saveBooleanData(this.requireContext(), NOTIFY_STATE, notify)

        }

        // 로그아웃 클릭
        binding.logoutButton.setOnClickListener {
            logoutDialog()
        }

        // 회원 탈퇴 클릭
        binding.deleteAccountButton.setOnClickListener {
            val userId = SharedPreferencesData.getLongData(this.requireContext(), Constants.USER_ID)
            deleteAccountDialog(userId)
        }


    }

    // 로그아웃 알림창 띄우기
    private fun logoutDialog() {
        val dialog =
            LogoutDialog(this)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.requireActivity().supportFragmentManager,
            "DeleteAccountDialog")
    }

    override fun logoutButtonClick() {
        RetrofitManager.instance.postLogout(this.requireContext())
        SharedPreferencesData.removeAllData(this.requireContext())
        val intent = Intent(context, LoginView::class.java)
        startActivity(intent)
        this.requireActivity().finish()
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


}