package com.demo.sharingapp.domain.chat.chatroom

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityChatRoomBinding
import com.demo.sharingapp.domain.chat.chatroom.data.ChatRoomData
import com.demo.sharingapp.domain.chat.chatroom.data.ChatSendCallBack
import com.demo.sharingapp.domain.chat.chatroom.data.GetChatRoomInfoData
import com.demo.sharingapp.domain.chat.chatroom.data.SendMessageResponse
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.retrofit.TokenAuthenticator
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.CHATROOM_NUMBER
import com.demo.sharingapp.utils.Constants.NICKNAME
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.PRODUCT_IMAGE
import com.demo.sharingapp.utils.Constants.PRODUCT_SHARE_PRICE
import com.demo.sharingapp.utils.Constants.PRODUCT_TITLE
import com.demo.sharingapp.utils.Constants.USER_ID
import com.demo.sharingapp.utils.Constants.USER_PROFILE
import com.demo.sharingapp.utils.ViewUtil.hideKeyboard
import com.demo.sharingapp.utils.ViewUtil.showKeyboard
import com.google.android.gms.common.api.Api.Client
import com.google.gson.Gson
import com.google.gson.JsonParser
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
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var chatRoomAdepter: ChatRoomAdepter
    private lateinit var linearLayoutManger: LinearLayoutManager
    private lateinit var binding: ActivityChatRoomBinding

    private var serverState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = intent.getStringExtra(USER_PROFILE) ?: ""
        val chatRoomNum = intent.getStringExtra(CHATROOM_NUMBER) ?: "0"
        val productId = intent.getLongExtra(PRODUCT_ID, 0)
        val userId = SharedPreferencesData.getLongData(this, USER_ID)
        var opponentProfile = ""


        Log.e("userId", userId.toString())
        chatRoomAdepter= ChatRoomAdepter(profile)
        linearLayoutManger = LinearLayoutManager(applicationContext).apply {
            reverseLayout = true
        }
        binding.chatRoomRecyclerView.apply {
            adapter = chatRoomAdepter
            layoutManager = linearLayoutManger
        }


        RetrofitManager.instance.getChatRoomData(this, chatRoomNum.toLong()){
            binding.titleTextView.text = it.title
            val currencyFormat = NumberFormat.getInstance(Locale.KOREA)
            val sharePrice = currencyFormat.format(it.sharePrice)
            binding.buyPriceTextView.text = sharePrice
            Glide.with(binding.productImageView)
                .load(it.productImage)
                .into(binding.productImageView)
            binding.nicknameTextView.text = it.opponentNickname + " 채팅"
            val chatList = it.chatList.reversed()

            chatRoomAdepter.submitList(chatList)

        }


//        binding.titleTextView.text = productTitle
//        binding.buyPriceTextView.text = productSharePrice
//        Glide.with(binding.productImageView)
//            .load(productImage)
//            .into(binding.productImageView)
//
//        binding.nicknameTextView.text = sendUserNickname + " 채팅"

        runBlocking { RetrofitManager.instance.postReissueMain(this@ChatRoomActivity) }

        var token = SharedPreferencesData.getData(this, ACCESS_TOKEN)

        val headerToken = StompHeader("Authorization", token)
        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomNo", chatRoomNum))
        headerList.add(headerToken)

        val sendHeaderList = arrayListOf<StompHeader>()
        sendHeaderList.add(StompHeader("chatRoomNo", chatRoomNum))
        sendHeaderList.add(headerToken)
        sendHeaderList.add(StompHeader("destination", "/pub/message"))


        val sockClient = initMessage(headerList, chatRoomNum, userId)


        binding.backButton.setOnClickListener {
            if (serverState) {
                sockClient.disconnect()
            }
            finish()
        }

        binding.sendButton.setOnClickListener {
            if (serverState) {
                val sendData = binding.chatEditText.text.toString()
                val data = JSONObject()
                data.put("chatRoomNo", chatRoomNum)
                data.put("contentType", "notice")
                data.put("content", sendData)
                data.put("senderNo", 1)
                data.put("productNo", productId)
                sockClient.send(StompMessage(StompCommand.SEND, sendHeaderList, data.toString()))
                    .subscribe()
                binding.chatEditText.text.clear()
                val a = listOf( GetChatRoomInfoData("", 0, 0, "", "", sendData, 0,0,true))

                chatRoomAdepter.submitList( a + chatRoomAdepter.currentList )


            } else {
                Toast.makeText(this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            }

        }

//        binding.root.setOnTouchListener { v, event ->
//            Log.e("aa","aa")
//            return@setOnTouchListener false
//        }

        chatRoomAdepter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                linearLayoutManger.smoothScrollToPosition(binding.chatRoomRecyclerView,null,0)
            }
        })






//        exampleTest()

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
//        chatRoomAdepter.submitList()
    }

    private fun initMessage(headerList: ArrayList<StompHeader>, chatRoomNum: String, userId: Long): StompClient {

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
//        val clientHeader = mapOf("Authorization" to token)

        val sockClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
            "ws://118.41.215.56:8081" + "/ws-stomp/websocket", null, client)

        sockClient.connect(headerList)

//        val headerTokenList = listOf<StompHeader>(headerToken)
        // 메시지 구독
        chatSubscribe(sockClient, headerList, chatRoomNum, userId)

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
        chatRoomNum: String,
        userId : Long
    ) {
        sockClient.topic("/sub/pub/$chatRoomNum", headerList).subscribe({
            // 성공적인 경우 처리

            val data = Gson().fromJson(it.payload, SendMessageResponse::class.java)
            if (data.senderNo == userId) { // 보내는 사람일때

                val chatSendCallBack = ChatSendCallBack(id =data.id,
                    chatRoomNo = data.chatRoomNo,
                    contentType = data.contentType,
                    content = data.content,
                    senderName = data.senderName,
                    senderNo = data.senderNo,
                    productNo = data.productNo,
                    sendTime = data.sendTime,
                    readCount = data.readCount,
                    senderLoginId = data.senderLoginId)
                RetrofitManager.instance.postChatSendCallBack(this, chatSendCallBack)
                Log.e("Asad", data.toString())
//                Log.e("Asad", chatSendCallBack.toString())
            }else{ // 상대방 일때
                val content = data.content
                val a = listOf( GetChatRoomInfoData("", 0, 0, "", "", content, 0,0,false,))

                chatRoomAdepter.submitList( a + chatRoomAdepter.currentList )
            }
            Log.e("Asad", data.content)

            Log.e("Asad", it.payload)
        }, {
            Log.e("sadfasf", it.message.toString())
        })
    }


    private fun isTokenExpiredError(throwable: Throwable): Boolean {
        // 토큰 만료와 관련된 오류 여부를 판단하는 로직
        return throwable.message?.contains("Token expired") == true
    }

    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y
        Log.e("aa",ev.toString())
        Log.e("aa", "Y 좌표: $y")
        val location = IntArray(2)
        binding.chatEditText.getLocationOnScreen(location)

        val buttonY = location[1]
        Log.e("aa", "Y chat 좌표: $buttonY")
        if(y < buttonY){
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
    }

}

