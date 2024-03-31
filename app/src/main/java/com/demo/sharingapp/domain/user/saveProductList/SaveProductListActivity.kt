package com.demo.sharingapp.domain.user.saveProductList

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySaveProductListBinding
import com.demo.sharingapp.databinding.ActivityShareProductListBinding
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class SaveProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveProductListBinding
    private lateinit var saveProductListAdapter: SaveProductListAdapter
    private var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveProductListAdapter = SaveProductListAdapter(){
            val intent = Intent(this@SaveProductListActivity,
                DetailedProductActivity::class.java).putExtra(Constants.PRODUCT_ID,it)
            startActivityForResult(intent, Constants.MOVE_DETAILED_CODE)
        }


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveProductRecyclerView.apply {
            adapter = saveProductListAdapter
            layoutManager = LinearLayoutManager(this@SaveProductListActivity)
        }

        initProduct()


    }

    private fun initProduct() {
        val latitude = SharedPreferencesData.getData(this, Constants.LATITUDE).toDouble()
        val longitude = SharedPreferencesData.getData(this, Constants.LONGITUDE).toDouble()
        RetrofitManager.instance.getMySaveProduct(this,
            longitude = longitude,
            latitude = latitude,
            page = page) {
            saveProductListAdapter.submitList(it.content)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MOVE_DETAILED_CODE && resultCode == Activity.RESULT_OK) {

            initProduct()

        }
    }
}