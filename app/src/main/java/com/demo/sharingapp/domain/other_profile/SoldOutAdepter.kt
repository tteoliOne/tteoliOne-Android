package com.demo.sharingapp.domain.other_profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ItemOtherSoldOutBinding
import com.demo.sharingapp.domain.other_profile.data.OtherProfileContent

class SoldOutAdepter:ListAdapter<OtherProfileContent, SoldOutAdepter.SoldOutViewHolder>(object :DiffUtil.ItemCallback<OtherProfileContent>(){
    override fun areItemsTheSame(
        oldItem: OtherProfileContent,
        newItem: OtherProfileContent,
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: OtherProfileContent,
        newItem: OtherProfileContent,
    ): Boolean {
        return oldItem == newItem
    }
}) {
    inner class SoldOutViewHolder(val binding: ItemOtherSoldOutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OtherProfileContent){
            Glide.with(binding.imageView)
                .load(item.imageUrl)
                .into(binding.imageView)

            binding.titleTextView.text = item.title

            binding.priceTextView.text = String.format("개당 %d원", item.unitPrice)

            // 좋아요 개수
            binding.heartScoreTextView.text = item.totalLikes.toString()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoldOutViewHolder {
        return SoldOutViewHolder(ItemOtherSoldOutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SoldOutViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}