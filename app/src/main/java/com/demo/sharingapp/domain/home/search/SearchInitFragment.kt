package com.demo.sharingapp.domain.home.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.FragmentSearchInitBinding
import com.demo.sharingapp.domain.home.search.data.SearchInitData
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants
import com.demo.sharingapp.utils.Constants.SEARCH_RECODE

class SearchInitFragment : Fragment(R.layout.fragment_search_init) {
    private lateinit var binding: FragmentSearchInitBinding

    private lateinit var searchInitAdepter: SearchInitAdepter
    private var searchInitInterface: SearchInitInterface? = null

    private var data: String? = null
    private var getData = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchInitBinding.bind(view)

        searchInitAdepter = SearchInitAdepter(onRemove = {
            getData.remove(it)
            sharedSaveData()
            submitListAdepter()

        }, onTitle = {
            searchInitInterface= activity as? SearchInitInterface
            searchInitInterface?.rememberData(it)

        })
        binding.searchInitRecyclerView.apply {
            adapter = searchInitAdepter
            layoutManager = LinearLayoutManager(this@SearchInitFragment.requireContext())
        }

        if (SharedPreferencesData.containsData(this.requireContext(), Constants.SEARCH_RECODE)) {
            // 데이터가 있는지 확인
            val i = SharedPreferencesData.getSetData(this@SearchInitFragment.requireContext(),
                SEARCH_RECODE)
            if(i != null){
                getData = i as MutableSet<String>
            }

        }

        val receivedBundle = arguments
        if (receivedBundle != null) {
            data = receivedBundle.getString("key")
            if (data != null){
                getData.add(data!!)
                sharedSaveData()
            }
        }



        submitListAdepter()

        binding.allRemoveButton.setOnClickListener {
            if (SharedPreferencesData.containsData(this.requireContext(),
                    Constants.SEARCH_RECODE)
            ) {
                SharedPreferencesData.removeData(this@SearchInitFragment.requireContext(),
                    SEARCH_RECODE)
                searchInitAdepter.submitList(null)

            }
        }


    }

    private fun sharedSaveData() {
        SharedPreferencesData.saveSetData(this@SearchInitFragment.requireContext(),
            SEARCH_RECODE, getData)
    }

    private fun submitListAdepter() {
        val i = getData.map {
            SearchInitData(it)
        }
        searchInitAdepter.submitList(i)
    }


}