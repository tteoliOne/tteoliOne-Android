package com.demo.sharingapp.domain.chat.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityChatRoomBinding
import com.demo.sharingapp.domain.chat.ChatListAdepter
import com.demo.sharingapp.domain.chat.chatroom.data.ChatRoomData
import com.demo.sharingapp.domain.chat.data.ChatListData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

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

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val headers = mutableMapOf<String, String>()

        headers["chatRoomNo"] = "1"
        headers["Authorization"] = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MzUwNjU0NDk1Mn0.LeSt9ARQ3aLAlqgZgwnWE4f2PkjCpDmJ9_zXRscqUDctulttCJQACGH1ZYCbE-cwH4xAM13OwX7TXkNzuB25Jw"


        val sockClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
            "ws://118.41.215.56:8081" + "/ws-stomp/websocket",null,client)


        val isUnexpectedClosed = AtomicBoolean(false)

            Log.e("sadfasf",StompHeader("Authorization","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MzUwNjU0NDk1Mn0.LeSt9ARQ3aLAlqgZgwnWE4f2PkjCpDmJ9_zXRscqUDctulttCJQACGH1ZYCbE-cwH4xAM13OwX7TXkNzuB25Jw").toString())
//        sockClient.connect()
        val data = listOf<StompHeader>(StompHeader("Authorization","eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MzUwNjU0NDk1Mn0.LeSt9ARQ3aLAlqgZgwnWE4f2PkjCpDmJ9_zXRscqUDctulttCJQACGH1ZYCbE-cwH4xAM13OwX7TXkNzuB25Jw"))

        sockClient.topic("/sub/pub/1",data).subscribe {

            Log.e("Stomp", it.payload.toString())

        }

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomNo","1"))
        headerList.add(StompHeader("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MzUwNjU0NDk1Mn0.LeSt9ARQ3aLAlqgZgwnWE4f2PkjCpDmJ9_zXRscqUDctulttCJQACGH1ZYCbE-cwH4xAM13OwX7TXkNzuB25Jw"))
        sockClient.connect(headerList)



        sockClient.lifecycle().subscribe {
            when (it.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.e("Stomp", "서버 연결")
                    Log.e("Stomp", isUnexpectedClosed.toString())
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.e("Stomp", "서버 닫음")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("Stomp", "에러")
                    it.exception.toString()
                }
               else -> {
                    Log.d("else", it.message)
                }
            }

        }

        binding.requestButton.setOnClickListener {
            val data = JSONObject()
            data.put("id", "1")
            data.put("chatRoomNo", "1")
            data.put("contentType", "notice")
            data.put("content", "하이")
            data.put("senderName", "카리나")
            data.put("senderNo", 1)
            data.put("sendTime", 1643000000)
            data.put("productNo", 1)
            data.put("readCount", 1)
            data.put("senderLoginId", "aws5624")
//            sockClient.send("/pub/message", data.toString()).subscribe()
            headerList.add(StompHeader(StompHeader.DESTINATION,"/pub/message"))

            sockClient.send(StompMessage(StompCommand.SEND,headerList,data.toString())).subscribe()


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