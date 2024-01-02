package com.demo.sharingapp.domain.user.shareProductList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ActivityShareProductListBinding
import com.demo.sharingapp.retrofit.RetrofitManager
import com.demo.sharingapp.shared.SharedPreferencesData
import com.demo.sharingapp.utils.Constants.LATITUDE
import com.demo.sharingapp.utils.Constants.LONGITUDE

class ShareProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShareProductListBinding

    private var page = 0
    private var last = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shareProductListAdepter = ShareProductListAdepter(){
            RetrofitManager.instance.getRemoveProduct(context = this, productsId = it)
        }

        val linearLayoutManager = LinearLayoutManager(this@ShareProductListActivity)
        val latitude = SharedPreferencesData.getData(this, LATITUDE).toDouble()
        val longitude = SharedPreferencesData.getData(this, LONGITUDE).toDouble()

        // ItemTouchHelper.Callback 을 리사이클러뷰와 연결
        val swipeHelper = SwipeHelper()  // ItemTouchHelper.Callback 구현 클래스
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.ShareProductRecyclerView) // rvData = 리사이클러뷰 id

        binding.ShareProductRecyclerView.apply {
            adapter = shareProductListAdepter
            layoutManager = linearLayoutManager
        }

        binding.ShareProductRecyclerView.addOnScrollListener(object :
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
            status = null
        ) {
            shareProductListAdepter.submitList(it.content)

            if (page > 0) {
                this.last = it.last
                shareProductListAdepter.submitList(shareProductListAdepter.currentList + it.content.orEmpty())
            } else {
                shareProductListAdepter.submitList(it.content) {
                    binding.ShareProductRecyclerView.scrollToPosition(0)
                }

            }
        }
    }
}