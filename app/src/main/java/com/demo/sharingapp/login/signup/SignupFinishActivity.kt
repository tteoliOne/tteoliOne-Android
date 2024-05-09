package com.demo.sharingapp.login.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.sharingapp.databinding.ActivitySignupFinishBinding
import com.demo.sharingapp.login.LoginView

class SignupFinishActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignupFinishBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finishButton.setOnClickListener {
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
            finish()
        }
    }
}