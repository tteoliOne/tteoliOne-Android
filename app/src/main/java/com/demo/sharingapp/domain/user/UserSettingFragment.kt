package com.demo.sharingapp.domain.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentUserSettingBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.NICKNAME

class UserSettingFragment : Fragment(R.layout.fragment_user_setting) {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentUserSettingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserSettingBinding.bind(view)

        mainViewModel = ViewModelProvider(this.requireActivity()).get(MainViewModel::class.java)

        binding.saveButton.setOnClickListener {

            val accessToken =
                SharedPreferencesData.getData(this.requireContext(), Constants.ACCESS_TOKEN)
            val nickname = binding.nicknameEditText.text
            mainViewModel.updateValue(NICKNAME, nickname.toString())
            Log.e("nick", nickname.toString())
            RetrofitManager.instance.patchChangeNickname(this@UserSettingFragment.requireContext(),
                accessToken,
                nickname.toString()){success, message ->
                if (success){
                    beforeFragment()  // 이전 프레그먼트로 이동 함수 호출
                }else {
                    Toast.makeText(this@UserSettingFragment.requireContext(),message,Toast.LENGTH_SHORT).show()
                }
            }


        }


        binding.cancelButton.setOnClickListener {

            // 이전 프레그먼트로 이동 함수 호출
            beforeFragment()
        }


    }

    // 이전 프레그먼트로 이동 함수
    private fun beforeFragment() {
        findNavController().popBackStack()
    }

}