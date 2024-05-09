package com.demo.sharingapp.domain.home.other_profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ItemHomeProductBinding
import com.demo.sharingapp.databinding.ItemOtherOnsellBinding
import com.demo.sharingapp.domain.home.other_profile.data.OtherProfileContent
import com.demo.sharingapp.login.data.ProductsData
import okhttp3.internal.format

class OnSellAdepter(private val onViewClick:(Long)->Unit, private val onLikeClick:(Long)->Unit): ListAdapter<OtherProfileContent, OnSellAdepter.OnSellViewHolder>(object :DiffUtil.ItemCallback<OtherProfileContent>(){
    override fun areItemsTheSame(oldItem: OtherProfileContent, newItem: OtherProfileContent): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: OtherProfileContent, newItem: OtherProfileContent): Boolean {
        return oldItem == newItem
    }
}) {
    inner class OnSellViewHolder(val binding: ItemOtherOnsellBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OtherProfileContent){

            var liked = item.liked
            var likePoint = item.totalLikes

            Glide.with(binding.imageView)
                .load(item.imageUrl)
                .into(binding.imageView)

            // 상품 클릭
            binding.root.setOnClickListener {
                Log.e("click",item.productId.toString())
                onViewClick(item.productId)

            }

            binding.pickImageView.setOnClickListener {
                Log.e("liked", liked.toString())
                if (liked) {
                    binding.pickImageView.setImageResource(R.drawable.heart)
                    likePoint -= 1
                    binding.likeTextView.text = likePoint.toString()
                    liked = !liked
                } else {
                    binding.pickImageView.setImageResource(R.drawable.heart_fill)
                    likePoint += 1
                    binding.likeTextView.text = likePoint.toString()
                    liked = !liked
                }
                onLikeClick(item.productId)

            }

            binding.titleTextView.text = item.title

            binding.priceTextView.text = String.format("개당 %d원", item.unitPrice)

            // 좋아요 개수
            binding.likeTextView.text = item.totalLikes.toString()

            // 거리
            if (item.walkingDistance > 1000) {
                Log.e("거리 ", item.walkingDistance.toString())
                binding.walkingDistanceTextView.text =
                    String.format("%.1fkm 도보 %d분", item.walkingDistance / 1000, item.walkingTime)
            } else {
                binding.walkingDistanceTextView.text =
                    String.format("%.1fm 도보 %d분", item.walkingDistance, item.walkingTime)
            }

            // 좋아요 여부
            if (item.liked) { // 좋아요가 true 일때
                binding.pickImageView.setImageResource(R.drawable.heart_fill)
            } else { // 좋아요가 false 일때
                binding.pickImageView.setImageResource(R.drawable.heart)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnSellViewHolder {
        return OnSellViewHolder(ItemOtherOnsellBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    }

    override fun onBindViewHolder(holder: OnSellViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}