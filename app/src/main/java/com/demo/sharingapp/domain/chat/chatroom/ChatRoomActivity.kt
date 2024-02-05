package com.demo.sharingapp.domain.chat.chatroom

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityChatRoomBinding
import com.demo.sharingapp.domain.chat.chatroom.data.ChatRoomData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.retrofit.TokenAuthenticator
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.PRODUCT_IMAGE
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_PRICE
import com.demo.sharingapp.utils.Constants.PRODUCT_TITLE
import com.google.android.gms.common.api.Api.Client
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.internal.notify
import okhttp3.logging.HttpLoggingInterceptor
import org.java_websocket.client.WebSocketClient
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import ua.naiksoftware.stomp.provider.AbstractConnectionProvider
import ua.naiksoftware.stomp.provider.WebSocketsConnectionProvider
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var chatRoomAdepter: ChatRoomAdepter
    private lateinit var binding: ActivityChatRoomBinding

    private var serverState = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productTitle = intent.getStringExtra(PRODUCT_TITLE)
        val sendUserNickname = intent.getStringExtra(NICKNAME)
        val productSharePrice = intent.getStringExtra(PRODUCT_SHARE_PRICE)
        val productImage = intent.getStringExtra(PRODUCT_IMAGE)
        binding.titleTextView.text = productTitle
        binding.buyPriceTextView.text = productSharePrice
        Glide.with(binding.productImageView)
            .load(productImage)
            .into(binding.productImageView)

        binding.nicknameTextView.text = sendUserNickname + " 채팅"

        runBlocking { RetrofitManager.instance.postReissueMain(this@ChatRoomActivity) }

        var token = SharedPreferencesData.getData(this,ACCESS_TOKEN)

        val headerToken = StompHeader("Authorization", token)
        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomNo", "16"))
        headerList.add(headerToken)

        val sendHeaderList = arrayListOf<StompHeader>()
        sendHeaderList.add(StompHeader("chatRoomNo", "16"))
        sendHeaderList.add(headerToken)
        sendHeaderList.add(StompHeader("destination", "/pub/message"))


        val sockClient = initMessage(headerList)


        binding.backButton.setOnClickListener {
            if(serverState){
                sockClient.disconnect()
            }
            finish()
        }

        binding.sendButton.setOnClickListener {
            if (serverState){
                val sendData = binding.chatEditText.text.toString()
                val data = JSONObject()
                data.put("id", "1")
                data.put("chatRoomNo", "16")
                data.put("contentType", "notice")
                data.put("content", sendData)
                data.put("senderName", "카리나")
                data.put("senderNo", 1)
                data.put("sendTime", 1643000000)
                data.put("productNo", 1)
                data.put("readCount", 1)
                data.put("senderLoginId", "aws5624")
                sockClient.send(StompMessage(StompCommand.SEND,sendHeaderList,data.toString())).subscribe()
                binding.chatEditText.text.clear()
            }else{
                Toast.makeText(this, "오류가 발생하였습니다.",Toast.LENGTH_SHORT).show()
            }

        }


        chatRoomAdepter = ChatRoomAdepter()
        binding.chatRoomRecyclerView.apply {
            adapter = chatRoomAdepter
            layoutManager = LinearLayoutManager(this@ChatRoomActivity)
        }

        exampleTest()

    }

    private fun exampleTest() {
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

    private fun initMessage(headerList: ArrayList<StompHeader>): StompClient{

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
//        val clientHeader = mapOf("Authorization" to token)

        val sockClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
            "ws://118.41.215.56:8081" + "/ws-stomp/websocket",null,client)

        sockClient.connect(headerList)

//        val headerTokenList = listOf<StompHeader>(headerToken)
        // 메시지 구독
        chatSubscribe(sockClient, headerList)

        // 에러 메시지 구독
        errorSubscribe(sockClient, headerList)

        // stomp 생명주기
        stompLifecycle(sockClient)


        return sockClient

    }

    @SuppressLint("CheckResult")
    private fun stompLifecycle(sockClient: StompClient) {
        sockClient.lifecycle().subscribe {
            when (it.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.e("Stomp", "서버 연결")
                    serverState = true
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("Stomp", "에러")
                    Log.e("Stomp", it.exception.toString())
                    serverState = false

                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.e("Stomp", "서버 닫음")
                    serverState = false

                }
                else -> {

                }
            }

        }
    }

    // 에러 메시지 구독 함수
    @SuppressLint("CheckResult")
    private fun errorSubscribe(
        sockClient: StompClient,
        headerList: ArrayList<StompHeader>,
    ) {
        sockClient.topic("/pub/message", headerList).subscribe({ topicMessage ->
            if (topicMessage is StompMessage) {
                // 헤더 가져오기
                val headers = topicMessage.stompHeaders

                // 각 헤더에 접근하여 정보 출력 또는 사용
                for (header in headers) {
                    val headerName = header.key
                    val headerValue = header.value
                    // 헤더 정보를 로그에 출력 또는 사용
                    Log.e("StompHeader", "$headerName: $headerValue")
                    if (headerValue.equals("UNAUTHORIZED")) {
                        Log.e("오류", "오류 발생")
                        serverState = false
                        val reissueData =
                            runBlocking { RetrofitManager.instance.postReissueMain(this@ChatRoomActivity) }
                        if (reissueData) {
                            serverState = true
                            val token = SharedPreferencesData.getData(this, ACCESS_TOKEN)
                        }
                    }
                }

                // 페이로드 가져오기
                val payload = topicMessage.payload
                // 페이로드 정보를 로그에 출력 또는 사용
                Log.d("StompPayload", payload)
            }
        }, { throwable ->
            // 구독 중 오류가 발생한 경우
            Log.e("StompError", "Subscription error: ${throwable.message}")

        })
    }

    // 채팅 구독 함수
    @SuppressLint("CheckResult")
    private fun chatSubscribe(
        sockClient: StompClient,
        headerList: ArrayList<StompHeader>,
    ) {
        sockClient.topic("/sub/pub/16", headerList).subscribe({
            // 성공적인 경우 처리
            Log.e("Asad", it.payload.toString())
        }, {
            Log.e("sadfasf", it.message.toString())
        })
    }

    private fun connectStomp(token: String): Pair<StompClient, ArrayList<StompHeader>> {
        val sockClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
            "ws://118.41.215.56:8081" + "/ws-stomp/websocket")


        val isUnexpectedClosed = AtomicBoolean(false)

        Log.e("sadfasf",
            StompHeader("Authorization",
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MzUwNjU0NDk1Mn0.LeSt9ARQ3aLAlqgZgwnWE4f2PkjCpDmJ9_zXRscqUDctulttCJQACGH1ZYCbE-cwH4xAM13OwX7TXkNzuB25Jw").toString())
        val data = listOf<StompHeader>(StompHeader("Authorization", token))



        sockClient.topic("/sub/pub/1", data).subscribe({ topicMessage ->
            if (topicMessage is StompMessage) {
                // 헤더 가져오기
                val headers = topicMessage.stompHeaders

                // 각 헤더에 접근하여 정보 출력 또는 사용
                for (header in headers) {
                    val headerName = header.key
                    val headerValue = header.value
                    // 헤더 정보를 로그에 출력 또는 사용
                    Log.e("StompHeader", "$headerName: $headerValue")

                }

                // 페이로드 가져오기
                val payload = topicMessage.payload
                // 페이로드 정보를 로그에 출력 또는 사용
                Log.d("StompPayload", payload)
            }
        }, { throwable ->
            // 구독 중 오류가 발생한 경우
            Log.e("StompError", "Subscription error: ${throwable.message}")
            Log.e("StompError", "Subscription error: ${throwable.message}")

        })

        sockClient.topic("/pub/message", data).subscribe({ topicMessage ->
            if (topicMessage is StompMessage) {
                // 헤더 가져오기
                val headers = topicMessage.stompHeaders

                // 각 헤더에 접근하여 정보 출력 또는 사용
                for (header in headers) {
                    val headerName = header.key
                    val headerValue = header.value
                    // 헤더 정보를 로그에 출력 또는 사용
                    Log.e("StompHeader", "$headerName: $headerValue")
                    if(headerValue.equals("UNAUTHORIZED")){
                        Log.e("오류","오류 발생")
                        val reissueData =
                            runBlocking { RetrofitManager.instance.postReissueMain(this@ChatRoomActivity) }
                        if(reissueData){
                            val token = SharedPreferencesData.getData(this,ACCESS_TOKEN)
                            val (sockClient, headerList) = connectStomp(token)
                        }
                    }
                }

                // 페이로드 가져오기
                val payload = topicMessage.payload
                // 페이로드 정보를 로그에 출력 또는 사용
                Log.d("StompPayload", payload)
            }
        }, { throwable ->
            // 구독 중 오류가 발생한 경우
            Log.e("StompError", "Subscription error: ${throwable.message}")
            Log.e("StompError", "Subscription error: ${throwable.message}")

        })

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomNo", "1"))
        headerList.add(StompHeader("Authorization", token))
        sockClient.connect(headerList)

        sockClient.lifecycle().subscribe {
            when (it.type) {

                LifecycleEvent.Type.OPENED -> {
                    Log.e("Stomp", "서버 연결")
                    Log.e("Stomp", isUnexpectedClosed.toString())

                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("Stomp", "에러")
                    Log.e("Stomp", it.exception.toString())

                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.e("Stomp", "서버 닫음")
                    Log.e("message", "${it.message}")

                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT-> {
                    Log.e("message", "${it.message}")
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
            val headerList1 = arrayListOf<StompHeader>()
            headerList1.add(StompHeader("chatRoomNo","1"))
            headerList1.add(StompHeader("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhd3M1NjI0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY5OTgwNzc1N30.mlezbDa5KG7UQ3OZ7sHRlvJdSkTwUuM1aqqCP9kEw4giCHHkDI7R7P2CVkLdszvzWjkuuKB-B7vYZev4fkAzbg"))
            headerList.add(StompHeader(StompHeader.DESTINATION,"/pub/message"))



            sockClient.send(StompMessage(StompCommand.SEND,headerList,data.toString())).subscribe()
        }
        return Pair(sockClient, headerList)
    }

    private fun isTokenExpiredError(throwable: Throwable): Boolean {
        // 토큰 만료와 관련된 오류 여부를 판단하는 로직
        return throwable.message?.contains("Token expired") == true
    }
}
