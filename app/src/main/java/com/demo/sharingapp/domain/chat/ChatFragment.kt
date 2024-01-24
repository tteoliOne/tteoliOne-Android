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
import com.kakao.sdk.user.UserApiClient

class ChatFragment: Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatListAdepter: ChatListAdepter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        chatListAdepter = ChatListAdepter(){
            val intent = Intent(this.requireContext(), ChatRoomActivity::class.java)
            startActivity(intent)
        }

        binding.chatListRecyclerView.apply {
            adapter = chatListAdepter
            layoutManager = LinearLayoutManager(this@ChatFragment.requireContext())
        }

        val itemList = listOf(
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),
            ChatListData("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg", "윈터","양파 싸게 팔아요","언제 사셨나요?"),

            )
        chatListAdepter.submitList(itemList)


    }


}