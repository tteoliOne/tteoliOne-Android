package com.demo.sharingapp.domain.user

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentUserSettingBinding
import com.demo.sharingapp.domain.MainViewModel
import com.demo.sharingapp.domain.home.HomePartProductFragmentArgs
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.NICKNAME

class UserSettingFragment : Fragment(R.layout.fragment_user_setting) {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentUserSettingBinding
    private val args: UserSettingFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserSettingBinding.bind(view)

        mainViewModel = ViewModelProvider(this.requireActivity()).get(MainViewModel::class.java)

        val nickname = args.nickname
        val description = args.description
        val profileImage = args.profileImage

        binding.nicknameEditText.setText(nickname)
        if(description != ""){
            binding.IntroductionEditText.setText(description)
        }
        if (profileImage != ""){
            Glide.with(binding.userImageView)
                .load(profileImage)
                .circleCrop()
                .into(binding.userImageView)
        }


        binding.saveButton.setOnClickListener {
            val accessToken =
                SharedPreferencesData.getData(this.requireContext(), Constants.ACCESS_TOKEN)
            val nickname = binding.nicknameEditText.text
            val description = binding.IntroductionEditText.text
            mainViewModel.updateValue(NICKNAME, nickname.toString())
            Log.e("nick", nickname.toString())
            RetrofitManager.instance.patchChangeNickname(this@UserSettingFragment.requireContext(),
                accessToken,
                nickname.toString(),
                description.toString()
            ){success, message ->
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