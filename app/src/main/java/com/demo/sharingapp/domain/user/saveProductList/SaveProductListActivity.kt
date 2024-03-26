package com.demo.sharingapp.domain.user.saveProductList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySaveProductListBinding
import com.demo.sharingapp.databinding.ActivityShareProductListBinding
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class SaveProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveProductListBinding
    private var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val saveProductListAdapter = SaveProductListAdapter()
        val latitude = SharedPreferencesData.getData(this, Constants.LATITUDE).toDouble()
        val longitude = SharedPreferencesData.getData(this, Constants.LONGITUDE).toDouble()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveProductRecyclerView.apply {
            adapter = saveProductListAdapter
            layoutManager = LinearLayoutManager(this@SaveProductListActivity)
        }

        RetrofitManager.instance.getMySaveProduct(this, longitude = longitude, latitude = latitude, page = page){
            saveProductListAdapter.submitList(it.content)
        }


    }
}