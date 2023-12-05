package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupEmailBinding
import com.demo.sharingapp.login.data.TokenResponse
import com.demo.sharingapp.login.signup.data.EmailData
import com.demo.sharingapp.login.signup.data.EmailResponse
import com.demo.sharingapp.retrofit.RestAPI
import com.demo.sharingapp.retrofit.RetrofitClient
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.API
import com.demo.sharingapp.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignupEmailFragment:Fragment(R.layout.fragment_signup_email) {

    private lateinit var binding: FragmentSignupEmailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupEmailBinding.bind(view)

        // editText 가 형식에 맞게 적었는지 확인하는 함수 호출
        checkEditText()

        // 이전 버튼 클릭 함수 호출
        clickBackButton()

        // 다음 버튼 클릭 함수 호출
        clickNextButton()

    }

    // 이전 버튼 클릭 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            this.requireActivity().finish()
        }
    }

    // 다음 버튼 클릭 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {
            if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
                    binding.emailEditText.text)
            )
            //if (binding.emailEditText.text.contains("@"))
            {
                // 로딩바 띄우기 함수 호출
                showProgress()

                val email = EmailData(binding.emailEditText.text.toString())
                RetrofitManager.instance.postEmail(this.requireContext(),
                    email) { emailBoolean, message ->
                    if (emailBoolean) {

                        Toast.makeText(this@SignupEmailFragment.requireContext(),message,Toast.LENGTH_SHORT).show()
                        // 다음 화면으로 이동 함수 호출
                        moveNextScreen()
                        // 로딩바 내리기 함수 호출
                        hideProgress()
                    } else {
                        showDialog(message)
                        // 로딩바 내리기 함수 호출
                        hideProgress()
                    }

                }

            }
        }
    }

    // editText 가 형식에 맞게 적었는지 확인하는 함수
    private fun checkEditText() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("emailEditText", "$s $start $before $count")
                if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
                        binding.emailEditText.text)
                )
                //if (s != null && s.contains("@"))
                {
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

    // 다음 화면으로 이동 함수
    private fun moveNextScreen() {
        val action =
            SignupEmailFragmentDirections.actionSignupEmailFragmentToSignupEmailConfirmFragment()
        findNavController().navigate(action)
    }

    // 알림창 띄우기
    private fun showDialog(message: String) {
        val dialog = SignupDialog(message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@SignupEmailFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }

    // 로딩바 띄우기 함수
    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }
    // 로딩바 내리기 함수
    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }


}