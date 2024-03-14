package com.demo.sharingapp.domain.chat.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogReceiptBinding
import com.demo.sharingapp.databinding.DialogRequestBinding
import com.demo.sharingapp.databinding.DialogSignupBinding
import com.demo.sharingapp.login.signup.basic.ConfirmDialogInterface
import com.demo.sharingapp.login.signup.data.SignupData
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.ArrayList

class RequestDialog(
    requestDialogInterface: RequestDialogInterface,
    productId: Long,
    chatRoomNum: String,
    sockClient: StompClient,
    sendHeaderList: ArrayList<StompHeader>,
    contentType: String,
) : DialogFragment() {


    // 뷰 바인딩 정의
    private var _binding: DialogRequestBinding? = null
    private val binding get() = _binding!!
    private var requestDialogInterface: RequestDialogInterface? = null
    private var productId: Long? = null
    private var chatRoomNum: String? = null
    private var sockClient: StompClient? = null
    private var sendHeaderList: ArrayList<StompHeader>? = null
    private var contentType: String? = null


    init {
        this.requestDialogInterface = requestDialogInterface
        this.productId = productId
        this.chatRoomNum = chatRoomNum
        this.sockClient = sockClient
        this.sendHeaderList = sendHeaderList
        this.contentType = contentType
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

        // 요청하기 버튼 클릭
        binding.requestButtonLayout.setOnClickListener {
            this.requestDialogInterface?.onYesButtonClick(productId!!,chatRoomNum!!, sockClient!!, sendHeaderList!!, contentType!!)
            dismiss()
        }


        // 취소 버튼 클릭
        binding.cancelButtonLayout.setOnClickListener {
            dismiss()
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

interface RequestDialogInterface {
    fun onYesButtonClick(
        productId: Long,
        chatRoomNum: String,
        sockClient: StompClient,
        sendHeaderList: ArrayList<StompHeader>,
        contentType: String,
    )
}