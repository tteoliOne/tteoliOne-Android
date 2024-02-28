package com.demo.sharingapp.domain.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentChatBinding
import com.demo.sharingapp.domain.chat.chatroom.ChatRoomActivity
import com.demo.sharingapp.domain.chat.data.ChatListData
import com.demo.sharingapp.domain.home.HomePartProductAdepter
import com.demo.sharingapp.login.LoginView
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.CHATROOM_NUMBER
import com.demo.sharingapp.utils.Constants.MOVE_CHAT_CODE
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.USER_PROFILE
import com.kakao.sdk.user.UserApiClient

class ChatFragment: Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatListAdepter: ChatListAdepter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        chatListAdepter = ChatListAdepter(){
            val intent = Intent(this.requireContext(), ChatRoomActivity::class.java)
                .putExtra(CHATROOM_NUMBER,it.chatNo.toString())
                .putExtra(PRODUCT_ID, it.productNo)
                .putExtra(USER_PROFILE, it.participant.profile)
            startActivityForResult(intent,MOVE_CHAT_CODE)
        }

        binding.chatListRecyclerView.apply {
            adapter = chatListAdepter
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext())
        }

        // 채팅 리스트 불러오기
        getChatList()

    }

    // 채팅 리스트 불러오기 함수
    private fun getChatList() {
        RetrofitManager.instance.getChatList(this@ChatFragment.requireContext()) {
            chatListAdepter.submitList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MOVE_CHAT_CODE && resultCode == Activity.RESULT_OK) {
            getChatList()
        }
    }


}