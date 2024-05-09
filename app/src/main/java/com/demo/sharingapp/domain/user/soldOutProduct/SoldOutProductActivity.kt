package com.demo.sharingapp.domain.user.soldOutProduct

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivitySoldOutProductBinding
import com.demo.sharingapp.domain.home.part.DetailedProductActivity
import com.demo.sharingapp.domain.user.shareProductList.ShareProductListAdepter
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants

class SoldOutProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySoldOutProductBinding
    private lateinit var shareProductListAdepter: ShareProductListAdepter

    private var page = 0
    private var last = false
    private var longitude = 0.0
    private var latitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldOutProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shareProductListAdepter = ShareProductListAdepter(1, onDelete = {
            RetrofitManager.instance.getRemoveProduct(context = this, productsId = it)
        }, onViewClick = {
            val intent = Intent(this@SoldOutProductActivity,
                DetailedProductActivity::class.java).putExtra(Constants.PRODUCT_ID,it)
            startActivityForResult(intent, Constants.MOVE_DETAILED_CODE)
        }, onLikeClick = {
            likeClick(it)
        })
        val linearLayoutManager = LinearLayoutManager(this@SoldOutProductActivity)
        latitude = SharedPreferencesData.getData(this, Constants.LATITUDE).toDouble()
        longitude = SharedPreferencesData.getData(this, Constants.LONGITUDE).toDouble()



        binding.soldOutProductRecyclerView.apply {
            adapter = shareProductListAdepter
            layoutManager = linearLayoutManager
        }

        binding.soldOutProductRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalCount = linearLayoutManager.itemCount
                val lastVisiblePosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition >= (totalCount - 1) && !last && lastVisiblePosition > 28) {
                    Log.e("page", "aa $lastVisiblePosition , ${totalCount - 1}, $last")
                    page += 1
                    getShareProduct(
                        latitude,
                        longitude,
                        shareProductListAdepter,
                        page)
                }
            }
        })

        getShareProduct(latitude, longitude, shareProductListAdepter,0)

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun getShareProduct(
        latitude: Double,
        longitude: Double,
        shareProductListAdepter: ShareProductListAdepter,
        page: Int
    ) {
        RetrofitManager.instance.getShareProductList(
            context = this,
            latitude = latitude,
            longitude = longitude,
            sort = "createAt-desc",
            page = page,
            status = "eSoldOut"
        ) {
            shareProductListAdepter.submitList(it.content)

            if (page > 0) {
                this.last = it.last
                shareProductListAdepter.submitList(shareProductListAdepter.currentList + it.content.orEmpty())
            } else {
                shareProductListAdepter.submitList(it.content) {
                    binding.soldOutProductRecyclerView.scrollToPosition(0)
                }

            }
        }
    }

    // 좋아요 클릭 시 함수
    private fun likeClick(it: Long) {
        RetrofitManager.instance.postProductLike(this, it)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MOVE_DETAILED_CODE && resultCode == Activity.RESULT_OK) {

            getShareProduct(latitude, longitude, shareProductListAdepter,0)

        }
    }
}