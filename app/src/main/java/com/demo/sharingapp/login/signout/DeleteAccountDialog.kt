package com.demo.sharingapp.login.signout

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogDeleteAccountBinding

class DeleteAccountDialog(
    deleteAccountDialogInterface: DeleteAccountDialogInterface,
    userId: Long,
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogDeleteAccountBinding? = null
    private val binding get() = _binding!!
    private var deleteAccountDialogInterface: DeleteAccountDialogInterface? = null
    private var userId: Long? = null


    init {
        this.deleteAccountDialogInterface = deleteAccountDialogInterface
        this.userId = userId

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = DialogDeleteAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 요청하기 버튼 클릭
        binding.confirmButton.setOnClickListener {
            this.deleteAccountDialogInterface?.onDeleteAccountButtonClick(userId!!)
            dismiss()
        }


        // 취소 버튼 클릭
        binding.cancelButton.setOnClickListener {
            dismiss()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

interface DeleteAccountDialogInterface {
    fun onDeleteAccountButtonClick(
        userId: Long,
    )
}