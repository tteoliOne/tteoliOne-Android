package com.demo.sharingapp.login.find_id

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindIdEmailBinding

class FindIdEmailFragment: Fragment(R.layout.fragment_find_id_email) {

    private lateinit var binding : FragmentFindIdEmailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindIdEmailBinding.bind(view)

    }
}