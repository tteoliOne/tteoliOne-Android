package com.demo.sharingapp.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.demo.sharingapp.BuildConfig.KAKAO_API_KEY
import com.demo.sharingapp.databinding.ActivityLoginViewBinding
import com.demo.sharingapp.login.data.LoginData
import com.demo.sharingapp.login.data.LoginTokenData
import com.demo.sharingapp.login.find_id.FindIdActivity
import com.demo.sharingapp.login.find_password.FindPasswordActivity
import com.demo.sharingapp.login.signup.SignupProfileActivity
import com.demo.sharingapp.login.signup.basic.SignUpActivity
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.KAKAO_TOKEN
import com.demo.sharingapp.utils.Constants.LOGIN_TYPE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient


class LoginView : AppCompatActivity() {

    private lateinit var binding: ActivityLoginViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // id 와 password 가 빈값인지 확인
        checkIdAndPassword()

        // 카카오 버튼 클릭 함수 호출
        clickKakaoButton()

        // 로그인 버튼 클릭 함수 호출
        clickLogin()

        // 비밀번호 변경 버튼 클릭 함수 호출
        clickChangePassword()

        // 회원가입 버튼 클릭 함수 호출
        clickSignupButton()

        // 아이디 찾기 버튼 클릭 함수 호출
        clickFindIdButton()

    }

    // 아이디 찾기 버튼 클릭 함수
    private fun clickFindIdButton() {
        binding.findIdButton.setOnClickListener {
            startActivity(Intent(this, FindIdActivity::class.java))
        }
    }

    // 회원가입 버튼 클릭 함수
    private fun clickSignupButton() {
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // 비밀번호 변경 버튼 클릭 함수
    private fun clickChangePassword() {
        binding.changPasswordButton.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
    }

    // 로그인 버튼 클릭 함수
    private fun clickLogin() {
        binding.loginButton.setOnClickListener {

            val id = binding.idEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            Firebase.messaging.token.addOnCompleteListener {
                val fmcToken = it.result
                Log.e("fmcToken", fmcToken)
                val loginData = LoginData(id, password,fmcToken)
                RetrofitManager.instance.postLogin(this, loginData) { code, message ->
                    if (code == 0) {
                        val intent = Intent(this, UserPlace::class.java)
                        startActivity(intent)
                        finish()
                        // 일반 로그인 타입 설정
                        settingLoginType(0)

                    } else if(code == 1001){
                        Toast.makeText(this, "비밀번호를 다시 확인해주세요",Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun settingLoginType(type: Int) { // 일반 로그인: 0, 카카오 로그인: 1
        SharedPreferencesData.saveIntData(this, LOGIN_TYPE, type)
    }

    // editText 포커스 clear 함수
    private fun editTextFocus() {
        binding.idEditText.clearFocus()
        binding.passwordEditText.clearFocus()
    }

    // id 와 password 가 빈값인지 확인 함수
    private fun checkIdAndPassword() {
        // id 입력시 password 가 빈값인지 확인하는 함수 호출
        idChangedToNotNullPassword()

        // password 입력시 id 가 빈값인지 확인하는 함수 호출
        passwordChangedToNotNullId()
    }

    // password 입력시 id 가 빈값인지 확인하는 함수
    private fun passwordChangedToNotNullId() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().trim().isNotEmpty() && binding.idEditText.text.toString()
                        .isNotEmpty()
                ) {
                    binding.loginButton.isClickable = true
                    binding.loginButton.setBackgroundColor(Color.parseColor("#588F11"))
                } else {
                    binding.loginButton.isClickable = false
                    binding.loginButton.setBackgroundColor(Color.parseColor("#9B9A9A"))
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // id 입력시 password 가 빈값인지 확인하는 함수
    private fun idChangedToNotNullPassword() {
        binding.idEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().trim().isNotEmpty() && binding.passwordEditText.text.toString()
                        .isNotEmpty()
                ) {
                    binding.loginButton.isClickable = true
                    binding.loginButton.setBackgroundColor(Color.parseColor("#588F11"))
                } else {
                    binding.loginButton.isClickable = false
                    binding.loginButton.setBackgroundColor(Color.parseColor("#9B9A9A"))
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // 카카오 버튼 클릭 함수
    private fun clickKakaoButton() {
        binding.kakaoLoginButton.setOnClickListener {
            binding.loadingView.isVisible=true
            binding.loginButton.isVisible=false

            KakaoSdk.init(this, "${KAKAO_API_KEY}")
            //카카오로그인 함수 호출
            kakaoLogin()

        }
    }

    //카카오로그인 함수
    private fun kakaoLogin() {

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) { } else if (token != null) {

                Firebase.messaging.token.addOnCompleteListener {
                    val fmcToken = it.result
                    // 레트로핏에 토큰 보내는 함수 호출
                    Log.e("fcm",fmcToken)
                    postKaKaoAccessToken(token.accessToken, fmcToken)
                }


            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {

                    // post 에서 데이터 받아서 쉐어드프리퍼런스에 저장 함수 호출
                    Firebase.messaging.token.addOnCompleteListener {
                        val fmcToken = it.result
                        Log.e("fmcToken",fmcToken)
                        // 레트로핏에 토큰 보내는 함수 호출
                        postKaKaoAccessToken(token.accessToken, fmcToken)
                    }

                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }


    // 레트로핏에 토큰 보내는 함수
    private fun postKaKaoAccessToken(token: String, targetToken: String? =null ) {
        val accessTokenRequest = LoginTokenData(token,targetToken)

        RetrofitManager.instance.postKaKaoToken(this, token = accessTokenRequest){
            if(it){
                settingLoginType(1)
                // 위치 설정 화면 이동 함수 호출
                moveUserPlaceActivity()

            }else{
                val intent = Intent(this, SignupProfileActivity::class.java)
                    .putExtra(KAKAO_TOKEN,token)
                startActivity(intent)
                // 카카오 로그인 타입 설정

                finish()



            }
        }
    }

    // 위치 설정 화면 이동 함수
    private fun moveUserPlaceActivity() {
        val intent = Intent(this, UserPlace::class.java)
        startActivity(intent)

        finish()
    }


    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        editTextFocus()
        return super.dispatchTouchEvent(ev)
    }

//    fun loginGoogle(activity: Activity){
//        val webClientId = activity.getString(R.string.google_login_web_client_id)
//
//        var signInOptions =
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(webClientId)
//                .requestEmail().build()
//
//        googleLoginClient = GoogleSignIn.getClient(activity, signInOptions)
//
//        googleLoginClient?.let { client ->
//
//            client.signOut().addOnCompleteListener {
//                // 로그아웃 후 진행
//                activity.startActivityForResult(
//                    client.signInIntent,
//                    REQ_GOOGLE_LOGIN
//                )
//            }
//        }
//    }

    fun onLogin(client: GoogleSignInClient) {

        val signInIntent: Intent = client.signInIntent
        startActivityForResult(signInIntent, 10001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            10001 -> {
                try {

                        val task = GoogleSignIn.getSignedInAccountFromIntent(data) ?: return
                        Log.d("PASS", task.toString())
                        val boolean = GoogleSignIn.getLastSignedInAccount(this)
                        Log.d("PASS1", boolean.toString())
                        val account = task.result

                        Log.d("PASS", account.idToken ?: "")
                        Log.d("PASS", account.serverAuthCode ?: "")
                        Log.d("PASS", account.id ?: "")
                        Log.d("PASS", account.email ?: "")


                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }


}