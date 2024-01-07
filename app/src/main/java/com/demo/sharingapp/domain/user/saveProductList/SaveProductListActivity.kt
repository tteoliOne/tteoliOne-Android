package com.demo.sharingapp.domain.user.saveProductList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySaveProductListBinding
import com.demo.sharingapp.databinding.ActivityShareProductListBinding

class SaveProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveProductListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveProductRecyclerView.apply {
            adapter = SaveProductListAdapter()
            layoutManager = LinearLayoutManager(this@SaveProductListActivity)
        }


    }
}