package com.demo.sharingapp.domain.home

import android.graphics.Outline
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ItemHomePartProductBinding
import com.demo.sharingapp.domain.home.data.PartProductContent
import com.demo.sharingapp.login.data.ProductsData
import okhttp3.internal.format

class HomePartProductAdepter(private val onLikeClick: (Long) -> Unit, private val onViewClick:(Long)->Unit) :
    ListAdapter<PartProductContent, HomePartProductAdepter.HomePartProductViewHolder>(object :
        DiffUtil.ItemCallback<PartProductContent>() {
        override fun areItemsTheSame(oldItem: PartProductContent, newItem: PartProductContent): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: PartProductContent, newItem: PartProductContent): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class HomePartProductViewHolder(val binding: ItemHomePartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PartProductContent, position: Int) {

            binding.root.setOnClickListener {
                onViewClick(item.productId)
            }

            var liked = item.liked
            var likePoint = item.totalLikes

            Glide.with(binding.imageView)
                .load(item.imageUrl)
                .into(binding.imageView)

            if (liked) {
                binding.pickImageView.setImageResource(R.drawable.heart_fill)
            } else {
                binding.pickImageView.setImageResource(R.drawable.heart)
            }


            binding.priceTextView.text = String.format("개당 %d원",item.unitPrice)
            binding.titleTextView.text = item.title
            if (item.walkingDistance > 1000) {
                binding.walkingTextView.text =
                    String.format("%.1fkm 도보 %d분", item.walkingDistance/1000, item.walkingTime)
            } else{
                binding.walkingTextView.text =
                    String.format("%.1fm 도보 %d분", item.walkingDistance, item.walkingTime)
            }
            binding.likeTextView.text = item.totalLikes.toString()

            binding.pickImageView.setOnClickListener {
                Log.e("liked",liked.toString())
                if (liked){
                    binding.pickImageView.setImageResource(R.drawable.heart)
                    likePoint -= 1
                    binding.likeTextView.text = likePoint.toString()
                    liked = !liked
                }else{
                    binding.pickImageView.setImageResource(R.drawable.heart_fill)
                    likePoint += 1
                    binding.likeTextView.text = likePoint.toString()
                    liked = !liked
                }
                onLikeClick(item.productId)
                updateItem(position = position, liked, likePoint)
            }

        }
    }

    // 아이템을 수정하는 함수
    fun updateItem(position: Int, liked: Boolean, totalLikes: Int) {
        val currentList = currentList.toMutableList()
        currentList[position].liked = liked
        currentList[position].totalLikes = totalLikes
        submitList(currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePartProductViewHolder {
        return HomePartProductViewHolder(ItemHomePartProductBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))

    }

    override fun onBindViewHolder(holder: HomePartProductViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }
}