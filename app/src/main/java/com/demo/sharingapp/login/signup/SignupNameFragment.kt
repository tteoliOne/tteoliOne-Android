package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupNameBinding
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import com.demo.sharingapp.utils.Constants.SIGNUP_NAME

class SignupNameFragment: Fragment(R.layout.fragment_signup_name) {

    private lateinit var binding: FragmentSignupNameBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupNameBinding.bind(view)

        // 이름 비어 있는지 확인 함수 호출
        checkEmptyName()

        // 다음 버튼 클릭 시 함수 호출
        clickNextButton()

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.signupEmailFragment)
            SharedPreferencesData.removeData(this.requireContext(), SIGNUP_EMAIL)
        }
    }

    // 다음 버튼 클릭 시 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val action = SignupNameFragmentDirections.actionSignupNameFragmentToSignupIdFragment()
            findNavController().navigate(action)
            SharedPreferencesData.saveData(this.requireContext(), SIGNUP_NAME, name)
        }
    }

    // 이름 비어 있는지 확인 함수
    private fun checkEmptyName() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.nextButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.nextButton.isClickable = true
                } else {
                    binding.nextButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.nextButton.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}