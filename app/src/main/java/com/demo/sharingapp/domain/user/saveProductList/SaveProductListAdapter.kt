package com.demo.sharingapp.domain.user.saveProductList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ItemHomePartProductBinding
import com.demo.sharingapp.domain.home.data.PartProductContent

class SaveProductListAdapter(private val onViewClick:(Long)->Unit) :
    ListAdapter<PartProductContent, SaveProductListAdapter.SaveProductListViewHolder>(object :
        DiffUtil.ItemCallback<PartProductContent>() {
        override fun areItemsTheSame(
            oldItem: PartProductContent,
            newItem: PartProductContent,
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: PartProductContent,
            newItem: PartProductContent,
        ): Boolean {
            return oldItem == newItem
        }
    }) {
    inner class SaveProductListViewHolder(private val binding: ItemHomePartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:PartProductContent){
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
//                onLikeClick(item.productId)
//                updateItem(position = position, liked, likePoint)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveProductListViewHolder {
        return SaveProductListViewHolder(ItemHomePartProductBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: SaveProductListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}