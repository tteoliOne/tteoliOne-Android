package com.demo.sharingapp.login.find_id

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentFindIdFinalBinding
import com.demo.sharingapp.login.find_password.FindPasswordActivity

class FindIdFinalFragment: Fragment(R.layout.fragment_find_id_final) {

    private val args: FindIdFinalFragmentArgs by navArgs()

    private lateinit var binding: FragmentFindIdFinalBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFindIdFinalBinding.bind(view)

        val findId = args.findId

        binding.idTextView.text = findId

        binding.moveLoginButton.setOnClickListener {
            this@FindIdFinalFragment.requireActivity().finish()
        }

        binding.moveChangePasswordButton.setOnClickListener {
            startActivity(Intent(this@FindIdFinalFragment.requireActivity(),FindPasswordActivity::class.java))
            this@FindIdFinalFragment.requireActivity().finish()
        }


    }
}