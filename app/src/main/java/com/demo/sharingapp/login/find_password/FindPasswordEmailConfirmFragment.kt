package com.demo.sharingapp.login.find_password

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindPasswordEmailConfirmBinding
import com.demo.sharingapp.login.find_id.FindIdEmailConfirmFragmentArgs
import com.demo.sharingapp.login.find_password.data.FindPasswordEmailVerifyData
import com.demo.sharingapp.login.signup.basic.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager

class FindPasswordEmailConfirmFragment : Fragment(R.layout.fragment_find_password_email_confirm) {

    private val args: FindPasswordEmailConfirmFragmentArgs by navArgs()

    private lateinit var binding: FragmentFindPasswordEmailConfirmBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindPasswordEmailConfirmBinding.bind(view)

        val name = args.name
        val id = args.id
        val email = args.email

        // 입력한 코드가 7자리가 맞는지 확인 함수
        checkInputText()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 다음버튼 클릭 시 함수 호출
        clickNextButton(name, email, id)
    }

    private fun clickNextButton(name: String, email: String, id: String) {
        binding.confirmButton.setOnClickListener {
            if (binding.emailConfirmEditText.text.count() >= 7) {
                val code = binding.emailConfirmEditText.text.toString()
                val passwordEmailVerifyData = FindPasswordEmailVerifyData(name, email, id, code)
                RetrofitManager.instance.postFindPasswordEmailVerify(passwordEmailVerifyData) { checkBoolean, message ->
                    if (checkBoolean) {
                        val action =
                            FindPasswordEmailConfirmFragmentDirections.actionFindPasswordEmailConfirmFragmentToFindPasswordResetFragment(
                                name,
                                email,
                                id)
                        findNavController().navigate(action)
                    } else {
                        showDialog(message)
                    }

                }
            }
        }
    }

    // 입력한 코드가 7자리가 맞는지 확인 함수
    private fun checkInputText() {
        binding.emailConfirmEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.emailConfirmEditText.text.count() >= 7) {
                    binding.confirmButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.confirmButton.isClickable = true
                } else {
                    binding.confirmButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.confirmButton.isClickable = false
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
        dialog.show(this@FindPasswordEmailConfirmFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }

}