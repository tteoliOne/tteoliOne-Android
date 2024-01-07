package com.demo.sharingapp.login.signup.basic

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogSignupBinding
import com.demo.sharingapp.databinding.DialogSignupNicknameBinding
import com.demo.sharingapp.login.signup.data.SignupData

class SignupDialogNickname(
    confirmDialogInterface: ConfirmDialogInterface,
    signupData: SignupData
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogSignupNicknameBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: ConfirmDialogInterface? = null

    private var signupData: SignupData? = null

    init {
        this.confirmDialogInterface = confirmDialogInterface

        this.signupData = signupData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = DialogSignupNicknameBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        // 취소 버튼 클릭
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.confirmButton.setOnClickListener {
            this.confirmDialogInterface?.onYesButtonClick(signupData!!)
            dismiss()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
interface ConfirmDialogInterface {
    fun onYesButtonClick(signupData: SignupData)
}
