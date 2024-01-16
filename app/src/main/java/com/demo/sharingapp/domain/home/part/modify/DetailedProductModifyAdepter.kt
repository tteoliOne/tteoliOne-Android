package com.demo.sharingapp.domain.home.part.modify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.addproduct.AddProductImageData
import com.demo.sharingapp.databinding.ItemProductImageBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData


class DetailedProductModifyAdepter(val onItemClicked: (Int) -> Unit) :
    ListAdapter<DetailedImageData, DetailedProductModifyAdepter.ProductImageViewHolder>(
        object : DiffUtil.ItemCallback<DetailedImageData>() {
            override fun areItemsTheSame(
                oldItem: DetailedImageData,
                newItem: DetailedImageData,
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: DetailedImageData,
                newItem: DetailedImageData,
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    inner class ProductImageViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.productImageView.clipToOutline = true
            Glide.with(binding.productImageView)
                .load(currentList[position].uri)
                .into(binding.productImageView)

            binding.root.setOnClickListener {
                onItemClicked(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        return ProductImageViewHolder(ItemProductImageBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        holder.bind(position)
    }
}