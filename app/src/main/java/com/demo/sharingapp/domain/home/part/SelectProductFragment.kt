package com.demo.sharingapp.domain.home.part

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentHomePartProductBinding
import com.demo.sharingapp.databinding.FragmentHomeSelectProductBinding

class SelectProductFragment: Fragment(R.layout.fragment_home_select_product) {

    private lateinit var binding: FragmentHomeSelectProductBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeSelectProductBinding.bind(view)
    }
}