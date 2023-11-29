package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogSignupBinding

class SignupDialog(
    text: String,
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogSignupBinding? = null
    private val binding get() = _binding!!

    private var text: String? = null

    init {
        this.text = text
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DialogSignupBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.errorMessageTextView.text = text

        // 취소 버튼 클릭
        binding.button.setOnClickListener {
            dismiss()
        }


        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
