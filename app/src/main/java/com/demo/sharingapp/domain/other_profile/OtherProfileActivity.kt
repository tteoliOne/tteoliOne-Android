package com.demo.sharingapp.domain.other_profile

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityOtherProfileBinding
import com.demo.sharingapp.databinding.ActivitySearchBinding
import com.demo.sharingapp.login.data.ProductsData
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.utils.Constants.SELLER_ID
import kotlin.math.roundToInt

class OtherProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherProfileBinding
    private lateinit var onSellAdepter: OnSellAdepter
    private lateinit var navHostOtherProfileFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sellerId = intent.getLongExtra(SELLER_ID, 0L)

        binding.backButton.setOnClickListener {
            finish()
        }

        RetrofitManager.instance.getOtherProfileSimple(this, sellerId) {
            Glide.with(binding.profileImageView)
                .load(it.profile)
                .circleCrop()
                .into(binding.profileImageView)

            val nickname = it.nickname + "님 가게"
            val spannable = SpannableStringBuilder(nickname)
            val targetText = "님 가게"
            val start = nickname.indexOf(targetText)
            val end = start + targetText.length

            val color = ForegroundColorSpan(Color.BLACK)
            spannable.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.nicknameTextView.text = spannable

            binding.goodCountTextView.text = ((it.ddabongScore*10).roundToInt()/10.0).toString()

            binding.onSellTextView.text = getString(R.string.on_sell_count, it.newProductCount)
            binding.soldOutTextView.text =
                getString(R.string.sold_out_product_count, it.soldOutProductCount)
            binding.reviewTextView.text = getString(R.string.review_count, it.reviewCount)

            if (it.intro != null) {
                if(it.intro.isNotEmpty()){
                    binding.introTextView.text = it.intro
                }
            }
        }

        onSellAdepter = OnSellAdepter()





        navHostOtherProfileFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_other_profile_fragment) as NavHostFragment

        val bundle = Bundle()
        bundle.putLong("sellerId", sellerId)
        navHostOtherProfileFragment.navController.navigate(R.id.onSellFragment,bundle)


        binding.soldOutLayout.setOnClickListener {

            navHostOtherProfileFragment.navController.navigate(R.id.salesCompleteFragment)
            Log.e("bb", "bb")
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.soldOutLayout.backgroundTintList = newTintColor
            binding.soldOutTextView.setTextColor(Color.WHITE)

        }

        binding.onSellLayout.setOnClickListener {
            navHostOtherProfileFragment.navController.navigate(R.id.onSellFragment,bundle)
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.onSellLayout.backgroundTintList = newTintColor
            binding.onSellTextView.setTextColor(Color.WHITE)
        }

        binding.reviewLayout.setOnClickListener {
            navHostOtherProfileFragment.navController.navigate(R.id.reviewFragment)
            categoryAllInit()
            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
            binding.reviewLayout.backgroundTintList = newTintColor
            binding.reviewTextView.setTextColor(Color.WHITE)
        }

//        binding.reviewLayout.setOnClickListener {
//            categoryAllInit()
//            val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.app_main, theme))
//            binding.reviewLayout.backgroundTintList = newTintColor
//            binding.reviewTextView.setTextColor(Color.WHITE)
//        }
//
//        binding.onSellRecyclerView.apply {
//            adapter = onSellAdepter
//            layoutManager = GridLayoutManager(context, 2)
//        }


    }

    private fun categoryAllInit() {
        val newTintColor = ColorStateList.valueOf(resources.getColor(R.color.white, theme))
        binding.soldOutLayout.backgroundTintList = newTintColor
        binding.onSellLayout.backgroundTintList = newTintColor
        binding.reviewLayout.backgroundTintList = newTintColor
        binding.soldOutTextView.setTextColor(Color.BLACK)
        binding.onSellTextView.setTextColor(Color.BLACK)
        binding.soldOutTextView.setTextColor(Color.BLACK)
        binding.reviewTextView.setTextColor(Color.BLACK)

    }

}