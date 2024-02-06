package com.demo.sharingapp.domain.chat

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
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.kakao.sdk.user.UserApiClient

class ChatFragment: Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatListAdepter: ChatListAdepter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        chatListAdepter = ChatListAdepter(){
            val intent = Intent(this.requireContext(), ChatRoomActivity::class.java)
                .putExtra(Constants.NICKNAME,it.participant.username)
                .putExtra(Constants.PRODUCT_TITLE, it.productTitle)
                .putExtra(CHATROOM_NUMBER,it.chatNo.toString())
                .putExtra(PRODUCT_ID, it.productNo)
            startActivity(intent)
        }

        binding.chatListRecyclerView.apply {
            adapter = chatListAdepter
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext())
        }

        RetrofitManager.instance.getChatList(this@ChatFragment.requireContext()){
            chatListAdepter.submitList(it)
        }




    }


}