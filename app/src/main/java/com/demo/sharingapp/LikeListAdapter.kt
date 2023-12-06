package com.demo.sharingapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.data.SaveProductsData
import com.demo.sharingapp.databinding.ItemHomeProductBinding
import com.demo.sharingapp.databinding.ItemProductSaveBinding

class LikeListAdapter: ListAdapter<SaveProductsData, LikeListAdapter.LikeListViewHolder>(object :
    DiffUtil.ItemCallback<SaveProductsData>(){
    override fun areItemsTheSame(oldItem: SaveProductsData, newItem: SaveProductsData): Boolean {
        return oldItem.likedId == newItem.likedId
    }

    override fun areContentsTheSame(oldItem: SaveProductsData, newItem: SaveProductsData): Boolean {
        return oldItem == newItem
    }
}) {
    inner class LikeListViewHolder(private val binding:ItemProductSaveBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item: SaveProductsData){
            Glide.with(binding.saveImageView)
                .load(item.productImage)
                .circleCrop()
                .into(binding.saveImageView)
            binding.saveTextView.text = item.title
            binding.root.setOnClickListener {
                Log.e("click","click")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeListViewHolder {
        return LikeListViewHolder(
            ItemProductSaveBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LikeListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}