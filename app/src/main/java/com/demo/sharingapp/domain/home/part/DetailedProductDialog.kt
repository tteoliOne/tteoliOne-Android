package com.demo.sharingapp.domain.home.part

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.DialogReceiptBinding
import com.demo.sharingapp.databinding.DialogSignupBinding

class DetailedProductDialog(
    uri: String,
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogReceiptBinding? = null
    private val binding get() = _binding!!

    private var uri: String? = null

    init {
        this.uri = uri
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DialogReceiptBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.receiptImageView.clipToOutline = true
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        Glide.with(this.requireContext())
            .load(uri)
            .into(binding.receiptImageView)

        // 취소 버튼 클릭
        binding.backButton.setOnClickListener {
            dismiss()
        }


        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}