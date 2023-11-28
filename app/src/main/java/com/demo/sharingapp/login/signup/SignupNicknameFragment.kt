package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupNicknameBinding
import com.demo.sharingapp.login.signup.data.SignupData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import com.demo.sharingapp.utils.Constants.SIGNUP_ID
import com.demo.sharingapp.utils.Constants.SIGNUP_NAME
import com.demo.sharingapp.utils.Constants.SIGNUP_PASSWORD

class SignupNicknameFragment: Fragment(R.layout.fragment_signup_nickname) {

    private lateinit var binding: FragmentSignupNicknameBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupNicknameBinding.bind(view)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
            SharedPreferencesData.removeData(this.requireContext(),SIGNUP_PASSWORD)
        }

        binding.finishButton.setOnClickListener {
            requireActivity().finish()
            val loginId = SharedPreferencesData.getData(this.requireContext(), SIGNUP_ID)
            val email = SharedPreferencesData.getData(this.requireContext(), SIGNUP_EMAIL)
            val username = SharedPreferencesData.getData(this.requireContext(), SIGNUP_NAME)
            val password = SharedPreferencesData.getData(this.requireContext(), SIGNUP_PASSWORD)
            val nickname = binding.nicknameEditText.text.toString()

            val signupData = SignupData(loginId,email,username,nickname,password)

            RetrofitManager.instance.postSignup(signupData){
                if (it){
                    Toast.makeText(this.requireContext(),"회원가입을 성공합니다", Toast.LENGTH_SHORT).show()
                }
            }

        }

        binding.nicknameEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()){
                    binding.finishButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.finishButton.isClickable = true
                }else{
                    binding.finishButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.finishButton.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
}