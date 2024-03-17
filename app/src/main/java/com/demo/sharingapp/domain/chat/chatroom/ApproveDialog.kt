package com.demo.sharingapp.domain.chat.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogRequestBinding
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.ArrayList

class ApproveDialog(
    approveDialogInterface: ApproveDialogInterface,
    productId: Long,
    chatRoomNum: String,
    sockClient: StompClient,
    sendHeaderList: ArrayList<StompHeader>,
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogRequestBinding? = null
    private val binding get() = _binding!!
    private var approveDialogInterface: ApproveDialogInterface? = null
    private var productId: Long? = null
    private var chatRoomNum: String? = null
    private var sockClient: StompClient? = null
    private var sendHeaderList: ArrayList<StompHeader>? = null


    init {
        this.approveDialogInterface = approveDialogInterface
        this.productId = productId
        this.sockClient = sockClient
        this.chatRoomNum = chatRoomNum
        this.sendHeaderList = sendHeaderList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = DialogRequestBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.titleTextView.text = "공유완료 요청 승인"
        binding.messageTextView.text = "공유완료 요청 승인 하시겠습니까?"
        binding.confirmButton.text = "승인하기"
        binding.cancelButton.text = "거절하기"

        // 요청하기 버튼 클릭
        binding.confirmButton.setOnClickListener {
            this.approveDialogInterface?.onApproveButtonClick(productId!!,
                chatRoomNum!!,
                sockClient!!,
                sendHeaderList!!)
            dismiss()
        }


        // 취소 버튼 클릭
        binding.cancelButton.setOnClickListener {
            this.approveDialogInterface?.onRejectButtonClick(productId!!,
                chatRoomNum!!,
                sockClient!!,
                sendHeaderList!!)
            dismiss()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

interface ApproveDialogInterface {
    fun onApproveButtonClick(
        productId: Long,
        chatRoomNum: String,
        sockClient: StompClient,
        sendHeaderList: ArrayList<StompHeader>,
    )

    fun onRejectButtonClick(
        productId: Long,
        chatRoomNum: String,
        sockClient: StompClient,
        sendHeaderList: ArrayList<StompHeader>
    )
}