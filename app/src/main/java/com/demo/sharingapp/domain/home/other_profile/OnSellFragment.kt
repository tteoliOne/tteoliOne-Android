package com.demo.sharingapp.domain.home.other_profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentOtherOnsellBinding
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class OnSellFragment: Fragment(R.layout.fragment_other_onsell) {

    private lateinit var binding: FragmentOtherOnsellBinding
    private lateinit var onSellAdepter: OnSellAdepter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtherOnsellBinding.bind(view)

        onSellAdepter = OnSellAdepter()

        binding.onSellRecyclerView.apply {
            adapter = onSellAdepter
            layoutManager = GridLayoutManager(context, 2)
        }

        val receivedBundle = arguments
        if (receivedBundle != null) {
            val sellerId = receivedBundle.getLong("sellerId")
            val longitude = receivedBundle.getDouble("longitude")
            val latitude = receivedBundle.getDouble("latitude")
            if(sellerId != 0L && longitude != 0.0 && latitude != 0.0){
                RetrofitManager.instance.getOtherProfile(this@OnSellFragment.requireContext(), sellerId = sellerId, longitude = longitude, latitude = latitude, page = 0, soldStatus = "eNew"){
                    onSellAdepter.submitList(it)
                }
            }

        }


//        val itemList = listOf(
//            ProductsData(1,"https://tteolione-bucket.s3.ap-northeast-2.amazonaws.com/test/afd14bab-a63f-4ddd-ae18-74d0f5092683.jpeg","과일",2000,3.0,3,3,false),
//            )
//
//        onSellAdepter.submitList(itemList)

    }

}