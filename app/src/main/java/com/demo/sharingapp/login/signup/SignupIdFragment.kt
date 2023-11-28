package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupIdBinding
import com.demo.sharingapp.login.signup.data.IdData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData

import com.demo.sharingapp.utils.Constants.SIGNUP_EMAIL
import com.demo.sharingapp.utils.Constants.SIGNUP_ID
import com.demo.sharingapp.utils.Constants.SIGNUP_NAME
import com.demo.sharingapp.utils.Constants.USER_ID
import java.util.regex.Pattern

class SignupIdFragment: Fragment(R.layout.fragment_signup_id) {

    private lateinit var binding: FragmentSignupIdBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupIdBinding.bind(view)

        // 아이디 유효성 검사 후 버튼 색 변경 함수 호출
        checkIdEditText()

        // 이전버튼 클릭 시 함수 호출
        clickBackButton()

        // 다음버튼 클릭 시 함수 호출
        clickNextButton()

    }

    // 아이디 유효성 검사 후 버튼 색 변경 함수
    private fun checkIdEditText() {
        binding.idEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Pattern.matches("^(?=.*[a-z])[a-z0-9]{6,20}\$", s)) {
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

    // 다음버튼 클릭 시 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {

            val id = binding.idEditText.text.toString()
            // id 유효성 검사 함수 호출
            checkId(id)

        }
    }

    // id 유효성 검사 함수
    private fun checkId(id: String) {
        if (Pattern.matches("^(?=.*[a-z])[a-z0-9]{6,20}\$",id)){

            RetrofitManager.instance.postCheckId(IdData(id)){ idBoolean, idMessage ->
                if (idBoolean){
                    SharedPreferencesData.saveData(this.requireContext(), SIGNUP_ID, id)
                    val action = SignupIdFragmentDirections.actionSignupIdFragmentToSignupPasswordFragment()
                    findNavController().navigate(action)
                }else{
                    Log.e("checkId", idMessage)
                }
            }


        }else{
            // todo 오류 알림창 표시
        }
    }

    // 이전버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            SharedPreferencesData.removeData(this.requireContext(), SIGNUP_NAME)
            findNavController().popBackStack()
        }
    }
}