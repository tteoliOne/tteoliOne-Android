package com.demo.sharingapp.domain.user.setting.account

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSettingPasswordBinding
import com.demo.sharingapp.retrofit.RetrofitManager
import java.util.regex.Pattern

class SettingPasswordFragment : Fragment(R.layout.fragment_setting_password) {
    private var oldPasswordType = false
    private var newPasswordType = false
    private var checkPasswordType = false
    private lateinit var binding: FragmentSettingPasswordBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingPasswordBinding.bind(view)

        // 기존 패스워드 입력값 확인하는 함수
        checkOldPasswordEtv()

        // 새 패스워드 입력값 확인하는 함수
        checkNewPasswordEtv()

        // 새 패스워드 확인 입력값 확인하는 함수
        checkCheckPasswordEtv()

        // 이전 버튼 클릭
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 변경 버튼 클릭
        binding.changeBtn.setOnClickListener {
            val oldPassword = binding.oldPasswordEtv.text.toString()
            val newPassword = binding.newPasswordEtv.text.toString()
            val checkPassword = binding.checkPasswordEtv.text.toString()
            RetrofitManager.instance.patchChangePassword(context = this.requireContext(),
                oldPassword = oldPassword,
                newPassword = newPassword,
                checkPassword = checkPassword){ code, message ->
                if (code == 0){
                    findNavController().popBackStack()
                    Toast.makeText(this.requireContext(),"비밀번호 재설정 성공",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.requireContext(),message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun checkCheckPasswordEtv() {
        binding.checkPasswordEtv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.newPasswordEtv.text.toString() != binding.checkPasswordEtv.text.toString()) {
                    binding.checkPasswordEtl.error = "비밀번호가 일치하지 않습니다."
                    checkPasswordType = false
                } else {
                    binding.checkPasswordEtl.isErrorEnabled = false
                    checkPasswordType = true

                }
                checkAllBtn()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkNewPasswordEtv() {
        binding.newPasswordEtv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8 || s.toString().length > 16) {
                    binding.newPasswordEtl.error = "8~16자 이내로 입력해 주십시오."
                    newPasswordType = false
                } else if (!Pattern.matches(".*[0-9].*", s) || !Pattern.matches(".*[a-z].*",
                        s) || !Pattern.matches(".*[^a-zA-Z0-9].*",
                        s) || Pattern.matches(".*[\\s].*", s)
                ) {
                    binding.newPasswordEtl.error = "숫자, 특수문자, 소문자, 공백미포함으로 적어주세요."
                    newPasswordType = false
                }else if (binding.newPasswordEtv.text.toString() == binding.oldPasswordEtv.text.toString()){
                    binding.newPasswordEtl.error = "새 비밀번호는 기존 비밀번호랑 다르게 입력해주세요."
                    newPasswordType = false
                }
                    else {
                    binding.newPasswordEtl.isErrorEnabled = false
                    newPasswordType = true

                }

                if (binding.newPasswordEtv.text.toString() != binding.checkPasswordEtv.text.toString()) { //새 비밀번호 확인 체크
                    binding.checkPasswordEtl.error = "비밀번호가 일치하지 않습니다."
                    checkPasswordType = false
                } else {
                    binding.checkPasswordEtl.isErrorEnabled = false
                    checkPasswordType = true

                }
                checkAllBtn()

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkOldPassword(s: CharSequence?){
        if (s.toString().trim().isEmpty()) {
            binding.oldPasswordEtl.error = "비밀번호는 필수정보입니다."
            oldPasswordType = false
        } else if (s.toString().length < 8 || s.toString().length > 16){
            binding.oldPasswordEtl.error = "8~16자 이내로 입력해 주십시오."
            oldPasswordType = false
        }else if (!Pattern.matches(".*[0-9].*", s) || !Pattern.matches(".*[a-z].*",
                s) || !Pattern.matches(".*[^a-zA-Z0-9].*",
                s) || Pattern.matches(".*[\\s].*", s)
        ){
            binding.oldPasswordEtl.error = "숫자, 특수문자, 소문자, 공백미포함으로 적어주세요."
            oldPasswordType = false
        }
        else if (binding.newPasswordEtv.text.toString() == binding.oldPasswordEtv.text.toString()){
            binding.newPasswordEtl.error = "새 비밀번호는 기존 비밀번호와 다르게 입력해주세요."
            newPasswordType = false
        }else {
            binding.oldPasswordEtl.isErrorEnabled = false
            oldPasswordType = true
        }
    }

    private fun checkOldPasswordEtv() {
        binding.oldPasswordEtv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkOldPassword(s)
                checkAllBtn()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun checkAllBtn() {
        if (oldPasswordType && newPasswordType && checkPasswordType) {
            binding.changeBtn.isClickable = true
            binding.changeBtn.setBackgroundColor(Color.parseColor("#588F11"))
        } else {
            binding.changeBtn.isClickable = false
            binding.changeBtn.setBackgroundColor(Color.parseColor("#CDCFCECE"))
        }
    }
}