package com.demo.sharingapp.domain.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemHomePartProductBinding
import com.demo.sharingapp.login.data.ProductsData
import okhttp3.internal.format

class HomePartProductAdepter :
    ListAdapter<ProductsData, HomePartProductAdepter.HomePartProductViewHolder>(object :
        DiffUtil.ItemCallback<ProductsData>() {
        override fun areItemsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class HomePartProductViewHolder(val binding: ItemHomePartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductsData) {
            Glide.with(binding.imageView)
                .load(item.imageUrl)
                .into(binding.imageView)

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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePartProductViewHolder {
        return HomePartProductViewHolder(ItemHomePartProductBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))

    }

    override fun onBindViewHolder(holder: HomePartProductViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}