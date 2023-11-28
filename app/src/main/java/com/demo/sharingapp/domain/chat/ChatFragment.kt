package com.demo.sharingapp.domain.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentChatBinding
import com.demo.sharingapp.login.LoginView
import com.kakao.sdk.user.UserApiClient

class ChatFragment: Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)


    }


}