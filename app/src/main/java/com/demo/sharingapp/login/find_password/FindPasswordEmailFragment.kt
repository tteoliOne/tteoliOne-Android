package com.demo.sharingapp.login.find_password

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
import com.demo.sharingapp.databinding.FragmentFindPasswordEmailBinding
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailData
import com.demo.sharingapp.login.signup.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import java.util.regex.Pattern

class FindPasswordEmailFragment : Fragment(R.layout.fragment_find_password_email) {
    private lateinit var binding: FragmentFindPasswordEmailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindPasswordEmailBinding.bind(view)

        // 입력시 유효성 검사
        inputCheckAll()

        // 다음 버튼 클릭 시 함수 호출
        clickNextButton()

        // 이전 버튼 클릭 함수 호출
        clickBackButton()
    }

    // 로딩바 띄우기 함수
    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }
    // 로딩바 내리기 함수
    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }

    // 이전 버튼 클릭 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            this@FindPasswordEmailFragment.requireActivity().finish()
        }
    }

    // 다음 버튼 클릭 시 함수
    private fun clickNextButton() {
        binding.nextButton.setOnClickListener {
            if (checkAll()) {
                showProgress()
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val id = binding.idEditText.text.toString()

                val passwordEmailData = FindPasswordEmailData(name, email, id)
                RetrofitManager.instance.postFindPasswordEmail(passwordEmailData) { checkBoolean, message ->
                    if (checkBoolean) {
                        val action =
                            FindPasswordEmailFragmentDirections.actionFindPasswordEmailFragmentToFindPasswordEmailConfirmFragment(
                                name,
                                id,
                                email)
                        findNavController().navigate(action)
                        hideProgress()
                        Toast.makeText(this.requireContext(),message,Toast.LENGTH_SHORT).show()
                    }else{
                        showDialog(message)
                        hideProgress()
                    }
                }
            }
        }
    }


    private fun inputCheckAll() {
        // 이름 입력시 유효성 검사 함수 호출
        inputNameToAllCheck()

        // id 입력시 유효성 검사 함수 호출
        inputIdToAllCheck()

        // email 입력시 유효성 검사 함수 호출
        inputEmailToAllCheck()
    }

    // email 입력시 유효성 검사 함수
    private fun inputEmailToAllCheck() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (checkAll()) {
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

    // id 입력시 유효성 검사 함수
    private fun inputIdToAllCheck() {
        binding.idEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (checkAll()) {
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

    // 이름 입력시 유효성 검사 함수
    private fun inputNameToAllCheck() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (checkAll()) {
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

    // 전체 유효성 검사
    private fun checkAll(): Boolean {
        return (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})",
            binding.emailEditText.text) && binding.nameEditText.text.toString()
            .isNotEmpty() && Pattern.matches("^(?=.*[a-z])[a-z0-9]{6,20}\$",
            binding.idEditText.text))
    }

    // 알림창 띄우기
    private fun showDialog(message: String) {
        val dialog = SignupDialog(message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@FindPasswordEmailFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }


}