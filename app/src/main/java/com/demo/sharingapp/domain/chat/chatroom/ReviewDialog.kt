package com.demo.sharingapp.domain.chat.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogReviewBinding

class ReviewDialog(
    reviewDialogInterface: ReviewDialogInterface,
    productId: Long,
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogReviewBinding? = null

    private val binding get() = _binding!!
    private var reviewDialogInterface: ReviewDialogInterface? = null
    private var productId: Long? = null

    init {
        this.reviewDialogInterface = reviewDialogInterface
        this.productId = productId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogReviewBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val countStr = binding.countTextView.text.toString()
        var countInt = countStr.toInt()

        binding.upButton.setOnClickListener {
            if (countInt<5){
                countInt += 1
                binding.countTextView.text = countInt.toString()
            }
            Log.d("count",countInt.toString())
        }

        binding.downButton.setOnClickListener {
            if (countInt>0){
                countInt -= 1
                binding.countTextView.text = countInt.toString()
            }
            Log.d("count",countInt.toString())
        }

        binding.descriptionEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length > 0){
                    binding.completeButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.completeButton.isClickable = true
                }else{
                    binding.completeButton.setBackgroundColor(Color.GRAY)
                    binding.completeButton.isClickable = false
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 요청하기 버튼 클릭
        binding.completeButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val goodCount = binding.countTextView.text.toString()
            this.reviewDialogInterface?.onCompleteButtonClick(productId!!,description,goodCount)
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

interface ReviewDialogInterface {
    fun onCompleteButtonClick(
        productId: Long,
        description: String,
        goodCount: String,
    )
}