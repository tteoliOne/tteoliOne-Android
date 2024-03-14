package com.demo.sharingapp.domain.chat.chatroom

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityChatRoomBinding
import com.demo.sharingapp.domain.chat.chatroom.data.*
import com.demo.sharingapp.login.signup.basic.SignupDialog
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.ACCESS_TOKEN
import com.demo.sharingapp.utils.Constants.CHATROOM_NUMBER
import com.demo.sharingapp.utils.Constants.PRODUCT_ID
import com.demo.sharingapp.utils.Constants.USER_ID
import com.demo.sharingapp.utils.Constants.USER_PROFILE
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import ua.naiksoftware.stomp.provider.ConnectionProvider
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class ChatRoomActivity : AppCompatActivity(), RequestDialogInterface {
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

        // 리사이클러뷰 초기설정
        initRecyclerView(profile)

        // 채팅 내역 정보 받아오기
        getChatRoomData(chatRoomNum)

        // 채팅예외처리
        chattingException()

        val sockClient = initMessage(chatRoomNum, userId, productId)

        // 이전 버튼 클릭
        clickBackButton(sockClient,chatRoomNum.toLong())

        // 메뉴 버튼 클릭
        clickMenuButton(chatRoomNum)

        chatRoomAdepter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                linearLayoutManger.smoothScrollToPosition(binding.chatRoomRecyclerView, null, 0)
            }
        })
        val sendHeaderList = arrayListOf<StompHeader>()
        val token = SharedPreferencesData.getData(this, ACCESS_TOKEN)
        val headerToken = StompHeader("Authorization", token)
        sendHeaderList.add(StompHeader("chatRoomNo", chatRoomNum))
        sendHeaderList.add(headerToken)
        sendHeaderList.add(StompHeader("destination", "/pub/message"))

        binding.requestButton.setOnClickListener {
            showDialog(productId,chatRoomNum, sockClient = sockClient, sendHeaderList = sendHeaderList, contentType = "notice" )
        }
        binding.noApproveButton.setOnClickListener {
            Log.e("button","승인")
        }

    }

    // 메뉴 버튼 클릭 함수
    private fun clickMenuButton(chatRoomNum: String) {
        binding.menuButton.setOnClickListener {
            val popup = PopupMenu(this@ChatRoomActivity, it)
            popup.menuInflater.inflate(R.menu.menu_chat, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.leaveMenu -> { // 나가기
                        Log.e("chatMenu", "나가기")
                        RetrofitManager.instance.deleteChatRoomLeave(this,
                            chatRoomId = chatRoomNum.toLong())
                        finish()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.reportMenu -> { // 신고하기
                        Log.e("chatMenu", "신고하기")
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popup.show()
        }
    }

    // 리사이클러뷰 초기설정 함수
    private fun initRecyclerView(profile: String) {
        chatRoomAdepter = ChatRoomAdepter(profile)
        linearLayoutManger = LinearLayoutManager(applicationContext).apply {
            reverseLayout = true
        }
        binding.chatRoomRecyclerView.apply {
            adapter = chatRoomAdepter
            layoutManager = linearLayoutManger
        }
    }

    // 채팅 내역 정보 받아오기 함수
    private fun getChatRoomData(chatRoomNum: String) {
        RetrofitManager.instance.getChatRoomData(this, chatRoomNum.toLong()) {

            // 채팅 제목
            binding.nicknameTextView.text = getString(R.string.nickname_input, it.opponentNickname)

            // 상품 이미지
            Glide.with(binding.productImageView)
                .load(it.productImage)
                .into(binding.productImageView)

            // 상품명
            binding.titleTextView.text = it.title

            // 개당 가격
            val sharePrice = changePrice(it)
            binding.buyPriceTextView.text = sharePrice

            if (it.checkSeller) { // 판매자일때
                binding.requestButton.visibility = View.INVISIBLE
                if(it.soldStatus == "eReservation"){
                    val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
                    binding.noApproveButton.backgroundTintList = newTintColor
                }else if (it.soldStatus == "eSolodOut"){
                    val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.gray, theme))
                    binding.noApproveButton.backgroundTintList = newTintColor
                    binding.noApproveButton.text = "공유 완료"
                }



            } else { // 소비자일때
                binding.noApproveButton.visibility = View.INVISIBLE
                if(it.soldStatus == "eReservation"){
                    binding.requestButton.text = "요청 중..."
                    binding.requestButton.isClickable = false
                }else if(it.soldStatus == "eSolodOut"){
                    val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.gray, theme))
                    binding.requestButton.text = "공유 완료"
                    binding.requestButton.backgroundTintList = newTintColor

                }

            }

            val productId = it.productId




            val chatList = it.chatList.reversed()



            chatRoomAdepter.submitList(chatList)

        }
    }

    // 가격 int -> 원화문자열로 변환
    private fun changePrice(it: GetChatRoomData) =
        NumberFormat.getInstance(Locale.KOREA).format(it.sharePrice)

    // 채팅예외처리 함수
    private fun chattingException() {
        binding.chatEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    binding.sendButton.setImageResource(R.drawable.chat_send_button)
                    binding.sendButton.isClickable = true
                } else {
                    binding.sendButton.setImageResource(R.drawable.chat_no_send_button)
                    binding.sendButton.isClickable = false
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Stomp 초기 설정(채팅) 함수
    private fun initMessage(
        chatRoomNum: String,
        userId: Long,
        productId: Long,
    ): StompClient {

        runBlocking { RetrofitManager.instance.postReissueMain(this@ChatRoomActivity) } // 채팅서버에 연결하기 전에 토큰 재발급

        val token = SharedPreferencesData.getData(this, ACCESS_TOKEN)
        val headerToken = StompHeader("Authorization", token)
        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("chatRoomNo", chatRoomNum))
        headerList.add(headerToken)

        val sendHeaderList = arrayListOf<StompHeader>()
        sendHeaderList.add(StompHeader("chatRoomNo", chatRoomNum))
        sendHeaderList.add(headerToken)
        sendHeaderList.add(StompHeader("destination", "/pub/message"))

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val sockClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
            "ws://43.200.94.118:8081" + "/ws-stomp/websocket", null, client)

        sockClient.connect(headerList)



        // 메시지 구독
        chatSubscribe(sockClient, headerList, chatRoomNum, userId)

        // 에러 메시지 구독
        errorSubscribe(sockClient, headerList)

        // stomp 생명주기
        stompLifecycle(sockClient)

        // 메시지 보내기
        sendMessage(chatRoomNum, productId, sockClient, sendHeaderList,"chat",userId)


        return sockClient

    }


    // stomp 생명주기 함수
    @SuppressLint("CheckResult")
    private fun stompLifecycle(sockClient: StompClient) {
        val dispLifecycle = sockClient.lifecycle().subscribe {
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
        userId: Long,
    ) {
        sockClient.topic("/sub/pub/$chatRoomNum", headerList).subscribe({
            // 성공적인 경우 처리

            val data = Gson().fromJson(it.payload, SendMessageResponse::class.java)
            Log.e("Asad", data.toString())
            if (data.contentType == "chat") {
                if (data.senderNo == userId) { // 보내는 사람일때
                    val chatSendCallBack = ChatSendCallBack(id = data.id,
                        chatRoomNo = data.chatRoomNo,
                        contentType = data.contentType,
                        content = data.content,
                        senderName = data.senderName,
                        senderNo = data.senderNo,
                        productNo = data.productNo,
                        sendTime = data.sendTime,
                        readCount = data.readCount,
                        senderLoginId = data.senderLoginId)
                    RetrofitManager.instance.postChatSendCallBack(this, chatSendCallBack){
                        val a = listOf(GetChatRoomInfoData("", 0, 0, it.data.senderName, "", it.data.content, it.data.sendTime, it.data.readCount.toLong(), true))

                        chatRoomAdepter.submitList(a + chatRoomAdepter.currentList)
                    }

//                Log.e("Asad", chatSendCallBack.toString())
                } else { // 상대방 일때
                    val content = data.content
                    val currentMillis = LocalDateTime.now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()?.toEpochMilli() ?: 0
                    val a = listOf(GetChatRoomInfoData("", 0, 0, "", "", content, currentMillis, 0, false))

                    chatRoomAdepter.submitList(a + chatRoomAdepter.currentList)
                }
            }else if(data.contentType == "notice"){
                if (data.content.contains("돌아오셨습니다")){
                    getChatRoomData(chatRoomNum)
                }
                else if(data.content.contains("true")){
                    val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
                    binding.noApproveButton.backgroundTintList = newTintColor
                }
            }

            Log.e("Asad", data.content)

            Log.e("Asad", it.payload)
        }, {
            Log.e("sadfasf", it.message.toString())
        })
    }

    // 메시지 보내기 함수
    private fun sendMessage(
        chatRoomNum: String,
        productId: Long,
        sockClient: StompClient,
        sendHeaderList: ArrayList<StompHeader>,
        contentType: String,
        userId: Long
    ) {
        binding.sendButton.setOnClickListener {
            if (serverState) {
                val currentMillis = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()?.toEpochMilli() ?: 0
                val sendData = binding.chatEditText.text.toString()
                val data = JSONObject()
                data.put("chatRoomNo", chatRoomNum)
                data.put("contentType", contentType)
                data.put("content", sendData)
                data.put("senderNo", userId)
                data.put("productNo", productId)
                sockClient.send(StompMessage(StompCommand.SEND, sendHeaderList, data.toString()))
                    .subscribe()
                binding.chatEditText.text.clear()
//                val a = listOf(GetChatRoomInfoData("", 0, 0, "", "", sendData, currentMillis, 0, true))
//
//                chatRoomAdepter.submitList(a + chatRoomAdepter.currentList)

            } else {
                Toast.makeText(this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // 이전 버튼 클릭 함수
    private fun clickBackButton(sockClient: StompClient,chatRoomNum: Long) {
        binding.backButton.setOnClickListener {

            if (serverState) {
                RetrofitManager.instance.putLeaveChatRoom(this, chatRoomId = chatRoomNum)
                sockClient.disconnect()
            }
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y
        Log.e("aa", ev.toString())
        Log.e("aa", "Y 좌표: $y")
        val location = IntArray(2)
        binding.chatEditText.getLocationOnScreen(location)

        val buttonY = location[1]
        Log.e("aa", "Y chat 좌표: $buttonY")
        if (y < buttonY) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        return super.dispatchTouchEvent(ev)
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

    // 알림창 띄우기
    private fun showDialog(productId: Long, chatRoomNum: String, sockClient: StompClient, sendHeaderList: ArrayList<StompHeader>, contentType: String ) {
        val dialog = RequestDialog(this, productId, chatRoomNum, sockClient, sendHeaderList,contentType)
        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager,
            "SignupDialog")
    }

    override fun onYesButtonClick(productId: Long, chatRoomNum: String, sockClient: StompClient, sendHeaderList: ArrayList<StompHeader>, contentType: String ) {
        RetrofitManager.instance.putProductRequest(this, productsId = productId){
            if (it){
                binding.requestButton.text = "요청 중..."
                binding.requestButton.isClickable = false
                val sendData = "true"
                val data = JSONObject()
                data.put("chatRoomNo", chatRoomNum)
                data.put("contentType", contentType)
                data.put("content", sendData)
                data.put("senderNo", 1)
                data.put("productNo", productId)
                sockClient.send(StompMessage(StompCommand.SEND, sendHeaderList, data.toString()))
                    .subscribe()
            }
        }

    }

}

