package com.demo.sharingapp.domain.chat.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityChatRoomBinding
import com.demo.sharingapp.domain.chat.ChatListAdepter
import com.demo.sharingapp.domain.chat.chatroom.data.ChatRoomData
import com.demo.sharingapp.domain.chat.data.ChatListData

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var chatRoomAdepter: ChatRoomAdepter
    private lateinit var binding: ActivityChatRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.titleTextView.text = "양파 싸게 팔아요"
        binding.buyPriceTextView.text = "개당 2,000원"
        Glide.with(binding.productImageView)
            .load("https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg")
            .circleCrop()
            .into(binding.productImageView)

        chatRoomAdepter = ChatRoomAdepter()
        binding.chatRoomRecyclerView.apply {
            adapter = chatRoomAdepter
            layoutManager = LinearLayoutManager(this@ChatRoomActivity)
        }
        val itemList = listOf(
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "언제 사셨나요?",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                1
            ),
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "한달정도 지났어요",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                2
            ),
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "혹시 네고 가능할까요?",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                1
            ),
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "당연히 안되죠",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                2
            ),
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "네",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                1
            ),
            ChatRoomData(
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/cb1e1376-4384-44a9-9dae-087d32d791ce.jpeg",
                "윈터",
                "양파 싸게 팔아요",
                "ㅋㅋㅋㅋㅋㅋ",
                10000,
                "https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/29087992-a912-41d6-9d84-2e96575120d8.jpeg",
                2
            ),

            )
        chatRoomAdepter.submitList(itemList)

    }
}