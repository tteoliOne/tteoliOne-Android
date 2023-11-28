package com.demo.sharingapp.login.signup

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSignupEmailBinding
import com.demo.sharingapp.login.data.TokenResponse
import com.demo.sharingapp.login.signup.data.EmailData
import com.demo.sharingapp.login.signup.data.EmailResponse
import com.demo.sharingapp.retrofit.RestAPI
import com.demo.sharingapp.retrofit.RetrofitClient
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.API
import com.demo.sharingapp.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignupEmailFragment:Fragment(R.layout.fragment_signup_email) {

    private lateinit var binding: FragmentSignupEmailBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupEmailBinding.bind(view)

        //
        Log.e("email", SharedPreferencesData.getData(this.requireContext(), Constants.SIGNUP_EMAIL))


        binding.emailEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("emailEditText","$s $start $before $count")
                if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})", binding.emailEditText.text))
                //if (s != null && s.contains("@"))
                {
                    binding.nextButton.setBackgroundColor(Color.parseColor("#588F11"))
                    binding.nextButton.isClickable = true
                }else{
                    binding.nextButton.setBackgroundColor(Color.parseColor("#CDCFCECE"))
                    binding.nextButton.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.backButton.setOnClickListener {
            this.requireActivity().finish()

        }

        binding.nextButton.setOnClickListener {
            if (Pattern.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})", binding.emailEditText.text))
            //if (binding.emailEditText.text.contains("@"))
            {
                val email = EmailData(binding.emailEditText.text.toString())
                RetrofitManager.instance.postEmail(this.requireContext(),email)
                val action =
                    SignupEmailFragmentDirections.actionSignupEmailFragmentToSignupEmailConfirmFragment()
                findNavController().navigate(action)
            }
        }

    }



}