package com.demo.sharingapp.login.find_id

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindIdEmailBinding
import com.demo.sharingapp.login.find_id.data.FindIdData
import com.demo.sharingapp.login.signup.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import java.util.regex.Pattern

class FindIdEmailFragment : Fragment(R.layout.fragment_find_id_email) {

    private lateinit var binding: FragmentFindIdEmailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindIdEmailBinding.bind(view)

        // 입력값 유효성 검사 함수 호출
        checkEditText()

        // 다음 버튼 클릭시 함수 호출
        clickNextButton()

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()

    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            this@FindIdEmailFragment.requireActivity().finish()
        }
    }

    // 다음 버튼 클릭시 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {

            // 유효성 검사
            if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
                    binding.emailEditText.text) && binding.nameEditText.text.toString().isNotEmpty()
            ) {
                showProgress()
                val email = binding.emailEditText.text.toString()
                val name = binding.nameEditText.text.toString()

                // 서버로 이메일과 이름 보내기
                RetrofitManager.instance.postFindIdEmail(FindIdData(name,
                    email)) { checkBoolean, message ->
                    if (checkBoolean) { // 성공 했을때
                        val action =
                            FindIdEmailFragmentDirections.actionFindIdEmailFragmentToFindIdEmailConfirmFragment(
                                name,
                                email)
                        findNavController().navigate(action)
                        hideProgress()
                        Toast.makeText(this@FindIdEmailFragment.requireContext(),
                            "인증코드가 발송 되었습니다.",
                            Toast.LENGTH_SHORT).show()
                    } else { // 실패 했을때
                        showDialog(message)
                        hideProgress()
                    }
                }
            }
        }
    }

    // 입력값 유효성 검사 함수
    private fun checkEditText() {
        // 이메일 입력 시 이름 유효성 검사 함수 호출
        changeEmailToNameCheck()
        // 이름 입력 시 이메일 유효성 검사 함수 호출
        changeNameToEmailCheck()
    }

    // 이름 입력 시 이메일 유효성 검사 함수
    private fun changeNameToEmailCheck() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim()
                        .isNotEmpty() && Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
                        binding.emailEditText.text)
                ) {
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

    // 이메일 입력 시 이름 유효성 검사 함수
    private fun changeEmailToNameCheck() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
                        binding.emailEditText.text)
                    && binding.nameEditText.text.toString().isNotEmpty()
                ) {
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

    // 로딩바 띄우기 함수
    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }
    // 로딩바 내리기 함수
    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }


    // 알림창 띄우기
    private fun showDialog(message: String) {
        val dialog = SignupDialog(message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@FindIdEmailFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }
}