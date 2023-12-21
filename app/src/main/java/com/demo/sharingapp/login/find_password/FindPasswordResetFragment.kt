package com.demo.sharingapp.login.find_password

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindPasswordResetBinding
import com.demo.sharingapp.login.find_password.data.FindPasswordResetData
import com.demo.sharingapp.login.signup.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import java.util.regex.Pattern

class FindPasswordResetFragment:Fragment(R.layout.fragment_find_password_reset) {

    private val args: FindPasswordResetFragmentArgs by navArgs()

    private lateinit var binding : FragmentFindPasswordResetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindPasswordResetBinding.bind(view)
        val name = args.name
        val id = args.id
        val email = args.email


        // 패스워드 입력시 유효성 검사 함수 호출
        checkInputPassword()

        // 다음 버튼 클릭 함수 호출
        clickNextButton(name, email, id)
    }

    // 다음 버튼 클릭 함수
    private fun clickNextButton(name: String, email: String, id: String) {
        binding.nextButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            val passwordResetData = FindPasswordResetData(name, email, id, password)
            RetrofitManager.instance.patchFindPasswordReset(passwordResetData) { checkBoolean, message ->
                if (checkBoolean) {
                    this@FindPasswordResetFragment.requireActivity().finish()
                    Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
                } else {
                    showDialog(message)
                }

            }
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

    // 알림창 띄우기
    private fun showDialog(message: String) {
        val dialog = SignupDialog(message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@FindPasswordResetFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }
}