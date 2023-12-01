package com.demo.sharingapp.login.find_id

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

class FindIdDialog(
    findIdDialogInterface: FindIdDialogInterface,
    message: String
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogSignupBinding? = null
    private val binding get() = _binding!!

    private var findIdDialogInterface: FindIdDialogInterface? = null


    private var text: String? = null


    init {
        this.findIdDialogInterface = findIdDialogInterface
        this.text = message
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = DialogSignupBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.errorMessageTextView.text = text

        // 확인 버튼 클릭
        binding.button.setOnClickListener {
            this.findIdDialogInterface?.onFindIdButtonClick()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
interface FindIdDialogInterface {
    fun onFindIdButtonClick()
}
