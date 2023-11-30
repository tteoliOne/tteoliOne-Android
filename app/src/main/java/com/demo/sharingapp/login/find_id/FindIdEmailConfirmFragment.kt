package com.demo.sharingapp.login.find_id

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindIdEmailConfirmBinding

class FindIdEmailConfirmFragment:Fragment(R.layout.fragment_find_id_email_confirm) {

    private lateinit var binding: FragmentFindIdEmailConfirmBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindIdEmailConfirmBinding.bind(view)
    }
}