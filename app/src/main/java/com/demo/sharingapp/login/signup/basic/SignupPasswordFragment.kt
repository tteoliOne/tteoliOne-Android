package com.demo.sharingapp.login.signup.basic

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupPasswordBinding
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.SIGNUP_ID
import com.demo.sharingapp.utils.Constants.SIGNUP_PASSWORD
import java.util.regex.Pattern

class SignupPasswordFragment: Fragment(R.layout.fragment_signup_password) {

    private lateinit var binding: FragmentSignupPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupPasswordBinding.bind(view)

        // 패스워드 입력시 유효성 검사 함수 호출
        checkInputPassword()

        // 다음 버튼 클릭 시 함수 호출
        clickNextButton()

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()
    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()

            SharedPreferencesData.removeData(this.requireContext(), SIGNUP_ID)
        }
    }

    // 다음 버튼 클릭 시 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {

            val password = binding.passwordEditText.text.toString()

            SharedPreferencesData.saveData(this.requireContext(),SIGNUP_PASSWORD, password)

            val action =
                SignupPasswordFragmentDirections.actionSignupPasswordFragmentToSignupNicknameFragment()
            findNavController().navigate(action)
        }
    }

    // 패스워드 입력시 유효성 검사 함수
    private fun checkInputPassword() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // 숫자가 포함되었는지 확인
                if (Pattern.matches(".*[0-9].*", s)) {
                    binding.numberCheckIcon.setColorFilter(Color.parseColor("#588F11"))
                    binding.numberCheckTextView.setTextColor(Color.BLACK)
                } else {
                    binding.numberCheckIcon.setColorFilter(Color.parseColor("#767676"))
                    binding.numberCheckTextView.setTextColor(Color.parseColor("#767676"))
                }

                // 소문자가 포함되었는지 확인
                if (Pattern.matches(".*[a-z].*", s)) {
                    binding.wordCheckIcon.setColorFilter(Color.parseColor("#588F11"))
                    binding.wordCheckTextView.setTextColor(Color.BLACK)
                } else {
                    binding.wordCheckIcon.setColorFilter(Color.parseColor("#767676"))
                    binding.wordCheckTextView.setTextColor(Color.parseColor("#767676"))
                }

                // 특수 문자가 있는지 확인
                if (Pattern.matches(".*[^a-zA-Z0-9].*", s)) {
                    binding.specialWordCheckIcon.setColorFilter(Color.parseColor("#588F11"))
                    binding.specialWordCheckTextView.setTextColor(Color.BLACK)
                } else {
                    binding.specialWordCheckIcon.setColorFilter(Color.parseColor("#767676"))
                    binding.specialWordCheckTextView.setTextColor(Color.parseColor("#767676"))
                }

                // 길이가 맞는지 확인
                if (s!!.length in 8..16) {
                    binding.lengthCheckIcon.setColorFilter(Color.parseColor("#588F11"))
                    binding.lengthCheckTextView.setTextColor(Color.BLACK)
                } else {
                    binding.lengthCheckIcon.setColorFilter(Color.parseColor("#767676"))
                    binding.lengthCheckTextView.setTextColor(Color.parseColor("#767676"))
                }

                // 공백이 있는지 확인
                if (Pattern.matches(".*[\\s].*", s)) {
                    binding.emptyCheckIcon.setColorFilter(Color.parseColor("#767676"))
                    binding.emptyCheckTextView.setTextColor(Color.parseColor("#767676"))
                } else {
                    binding.emptyCheckIcon.setColorFilter(Color.parseColor("#588F11"))
                    binding.emptyCheckTextView.setTextColor(Color.BLACK)
                }

                if(Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+\$).{8,16}", s)){
                    binding.nextButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.nextButton.isClickable = true
                } else{
                    binding.nextButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.nextButton.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}