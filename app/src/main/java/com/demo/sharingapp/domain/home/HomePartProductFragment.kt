package com.demo.sharingapp.domain.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentHomePartProductBinding
import com.demo.sharingapp.login.data.ProductsData

class HomePartProductFragment: Fragment(R.layout.fragment_home_part_product) {

    private val args: HomePartProductFragmentArgs by navArgs()

    private lateinit var homePartProductsAdepter: HomePartProductAdepter

    private lateinit var binding: FragmentHomePartProductBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomePartProductBinding.bind(view)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        homePartProductsAdepter = HomePartProductAdepter()

        val productsData: List<ProductsData> = args.data.toList()
        val category = args.name
        binding.categoryTextView.text = category
        Log.e("getProductsData", productsData.toString())

        homePartProductsAdepter.submitList(productsData)

        binding.partProductRecyclerView.apply {
            adapter = homePartProductsAdepter
            layoutManager = LinearLayoutManager(this@HomePartProductFragment.requireContext())
        }



    }
}