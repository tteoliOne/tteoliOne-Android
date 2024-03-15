package com.demo.sharingapp.domain.other_profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentOtherSoldOutBinding
import com.demo.sharingapp.retrofit.RetrofitManager

class SoldOutFragment: Fragment(R.layout.fragment_other_sold_out) {

    private lateinit var binding: FragmentOtherSoldOutBinding
    private lateinit var soldOutAdepter: SoldOutAdepter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtherSoldOutBinding.bind(view)

        soldOutAdepter = SoldOutAdepter()

        binding.soldOutRecyclerView.apply {
            adapter = soldOutAdepter
            layoutManager = GridLayoutManager(context, 2)
        }

        val receivedBundle = arguments
        if (receivedBundle != null) {
            val sellerId = receivedBundle.getLong("sellerId")
            val longitude = receivedBundle.getDouble("longitude")
            val latitude = receivedBundle.getDouble("latitude")
            if(sellerId != 0L && longitude != 0.0 && latitude != 0.0){
                RetrofitManager.instance.getOtherProfile(this@SoldOutFragment.requireContext(), sellerId = sellerId, longitude = longitude, latitude = latitude, page = 0, soldStatus = "eSoldOut"){
                    soldOutAdepter.submitList(it)
                }
            }

        }

    }
}