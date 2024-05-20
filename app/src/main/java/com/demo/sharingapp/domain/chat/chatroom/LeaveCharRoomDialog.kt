package com.demo.sharingapp.domain.chat.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.demo.sharingapp.databinding.DialogLeaveChatRoomBinding
import com.demo.sharingapp.databinding.DialogRequestBinding
import ua.naiksoftware.stomp.StompClient

class LeaveCharRoomDialog(
    leaveChatRoomDialogInterface: LeaveChatRoomDialogInterface,
    chatRoomNum: String,
    sockClient: StompClient
) : DialogFragment() {
    private var _binding: DialogLeaveChatRoomBinding? = null
    private val binding get() = _binding!!
    private var leaveChatRoomDialogInterface: LeaveChatRoomDialogInterface? = null
    private var chatRoomNum: String? = null
    private var sockClient: StompClient? = null

    init {
        this.leaveChatRoomDialogInterface = leaveChatRoomDialogInterface
        this.chatRoomNum = chatRoomNum
        this.sockClient = sockClient
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DialogLeaveChatRoomBinding.inflate(inflater, container, false)
        val view = binding.root

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 요청하기 버튼 클릭
        binding.confirmButton.setOnClickListener {
            this.leaveChatRoomDialogInterface?.leaveButtonClick(chatRoomNum!!,sockClient!!)
            dismiss()
        }


        // 취소 버튼 클릭
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}


interface LeaveChatRoomDialogInterface {
    fun leaveButtonClick(
        chatRoomNum: String,
        sockClient: StompClient
    )
}