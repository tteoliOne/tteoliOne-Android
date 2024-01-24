package com.demo.sharingapp.domain.home.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.NavHostFragment
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var navHostSearchFragment: NavHostFragment
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostSearchFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_signup_fragment) as NavHostFragment

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val data = binding.searchEditText.text.toString()
                val bundle = Bundle()
                bundle.putString("key", data)
                navHostSearchFragment.navController.navigate(R.id.searchResultFragment,bundle)
                true
            }else{
                false
            }
        }

    }
}