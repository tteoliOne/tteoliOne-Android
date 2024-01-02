package com.demo.sharingapp.addproduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.databinding.ItemProductImageBinding

class ProductImageAdapter(val onItemClicked: (Int) -> Unit) :
    ListAdapter<AddProductImageData, ProductImageAdapter.ProductImageViewHolder>(
        object : DiffUtil.ItemCallback<AddProductImageData>() {
            override fun areItemsTheSame(
                oldItem: AddProductImageData,
                newItem: AddProductImageData,
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: AddProductImageData,
                newItem: AddProductImageData,
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    inner class ProductImageViewHolder(private val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.productImageView.clipToOutline = true
            binding.productImageView.setImageURI(currentList[position].image)

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