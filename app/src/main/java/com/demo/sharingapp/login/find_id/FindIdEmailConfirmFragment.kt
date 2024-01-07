package com.demo.sharingapp.login.find_id

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindIdEmailConfirmBinding
import com.demo.sharingapp.login.find_id.data.FindIdEmailVerifyData
import com.demo.sharingapp.login.signup.basic.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import okhttp3.internal.format
import java.util.*
import kotlin.concurrent.timer

class FindIdEmailConfirmFragment : Fragment(R.layout.fragment_find_id_email_confirm),
    FindIdDialogInterface {



    private val args: FindIdEmailConfirmFragmentArgs by navArgs()

    // 타이머 초기 변수 설정
    private var timer: Timer? = null
    private var countdownMinutes = 3

    private lateinit var binding: FragmentFindIdEmailConfirmBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindIdEmailConfirmBinding.bind(view)


        val name = args.name
        val email = args.email

        // 입력한 코드가 7자리가 맞는지 확인 함수 호출
        checkEditText()

        // 다음 버튼 클릭 시 함수 호출
        clickNextButton(name, email)

        // 타이머 설정 함수 호출
        initTimer()

        // 이전 버튼 클릭 시 함수 호출
        clickBackButton()


    }

    // 이전 버튼 클릭 시 함수
    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            beforeScreen()
        }
    }

    // 다음 버튼 클릭 시 함수
    private fun clickNextButton(name: String, email: String) {
        binding.confirmButton.setOnClickListener {
            if (binding.emailConfirmEditText.text.count() >= 7) {

                val code = binding.emailConfirmEditText.text.toString()
                RetrofitManager.instance.postFindIdEmailVerify(findIdEmailVerifyData = FindIdEmailVerifyData(
                    name,
                    code,
                    email)){ checkBoolean, message, id ->
                    if (checkBoolean){
                        //showSuccessDialog(String.format("아이디 : %s 입니다.",id!!.loginId))
                        val action = FindIdEmailConfirmFragmentDirections.actionFindIdEmailConfirmFragmentToFindIdFinalFragment(id!!.loginId)
                        findNavController().navigate(action)
                    }else{
                        showDialog(message)
                    }

                }
            }
        }
    }

    // 입력한 코드가 7자리가 맞는지 확인 함수
    private fun checkEditText() {
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
    }

    // 알림창 띄우기
    private fun showDialog(message: String) {
        val dialog = SignupDialog(message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@FindIdEmailConfirmFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }
    // 알림창 띄우기
    private fun showSuccessDialog(message: String) {
        val dialog = FindIdDialog(this,message)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this@FindIdEmailConfirmFragment.requireActivity().supportFragmentManager,
            "SignupDialog")
    }

    override fun onFindIdButtonClick() {
        this@FindIdEmailConfirmFragment.requireActivity().finish()
        timer?.cancel()
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
    // 이전화면으로 이동 함수
    private fun beforeScreen() {

        timer?.cancel()
        // 이메일 삭제 함수 호출

        findNavController().popBackStack()


    }


}