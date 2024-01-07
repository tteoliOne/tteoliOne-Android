package com.demo.sharingapp.login.signup.basic

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupEmailConfirmBinding
import com.demo.sharingapp.login.signup.data.AuthCodeData
import com.demo.sharingapp.login.signup.data.EmailData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import okhttp3.internal.format
import java.util.*
import kotlin.concurrent.timer

class SignupEmailConfirmFragment : Fragment(R.layout.fragment_signup_email_confirm) {

    // 타이머 초기 변수 설정
    private var timer: Timer? = null
    private var countdownMinutes = 3

    private lateinit var binding: FragmentSignupEmailConfirmBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupEmailConfirmBinding.bind(view)



        //
        Log.e("email1",SharedPreferencesData.getData(this.requireContext(), SIGNUP_EMAIL))


        // 코드 7자리 입력했는지 확인
        binding.emailConfirmEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.count() >= 7) {
                    binding.confirmButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.confirmButton.isClickable = true
                } else {
                    binding.confirmButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.confirmButton.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // 완료버튼 클릭 시 함수 호출
        clickConfirmButton()


        // 타이머 설정 함수 호출
        initTimer()

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            // 이전화면으로 이동 함수 호출
            beforeScreen()
        }
    }

    // 이전화면으로 이동 함수
    private fun beforeScreen() {

        timer?.cancel()
        // 이메일 삭제 함수 호출
        removeEmail()

        findNavController().popBackStack()


    }

    // 이메일 삭제 함수
    private fun removeEmail() {
        SharedPreferencesData.removeData(this.requireContext(), SIGNUP_EMAIL)
    }

    // 타이머 설정 함수
    private fun initTimer() {

        // 분을 초로 바꿈
        var countdownSeconds = countdownMinutes * 60

        timer = timer(initialDelay = 0, period = 1000) {
            if (countdownSeconds <= 0) {

                // 이전화면으로 이동 함수 호출
                binding.root.post { beforeScreen() }

            } else {
                countdownSeconds -= 1
                val second = countdownSeconds % 60
                val minutes = (countdownSeconds / 60).toInt()
                binding.emailConfirmTimeTextView.post {
                    binding.emailConfirmTimeTextView.text = format("%02d:%02d", minutes, second)
                }

            }
        }
    }

    // 완료버튼 클릭 시 함수
    private fun clickConfirmButton() {
        binding.confirmButton.setOnClickListener {
            val emailCode = binding.emailConfirmEditText.text.toString()
            val email = SharedPreferencesData.getData(this.requireContext(), SIGNUP_EMAIL)
            val code = AuthCodeData(emailCode,email)
            RetrofitManager.instance.postEmailVerify(code,  nextScreen = {
                if (it){
                    nextScreen()
                }else{
                    binding.errorMessageTextView.isVisible=true
                }
            })
        }
    }

    private fun nextScreen(){
        val action =
            SignupEmailConfirmFragmentDirections.actionSignupEmailConfirmFragmentToSignupNameFragment()
        findNavController().navigate(action)
        timer?.cancel()
    }

}