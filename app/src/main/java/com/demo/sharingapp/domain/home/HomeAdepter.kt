package com.demo.sharingapp.domain.home

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.R
import com.demo.sharingapp.databinding.ItemHomeMoreBinding
import com.demo.sharingapp.databinding.ItemHomeProductBinding
import com.demo.sharingapp.login.data.ProductsData

class HomeAdepter(
    val onMoreClick: (List<ProductsData>) -> Unit,
    val onLikeClick: (Long) -> Unit,
    val onViewClick: (Long) -> Unit,
) :
    ListAdapter<ProductsData, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<ProductsData>() {
        override fun areItemsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem == newItem
        }
    }) {

    // 뷰홀더 클래스
    inner class HomeViewHolder(val binding: ItemHomeProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 뷰에 데이터 삽입 함수
        fun bind(item: ProductsData) {

            // 좋아요 여부 변수
            var liked = item.liked
            // 좋아요 개수 변수
            var likePoint = item.totalLikes

            // 이미지뷰에 이미지 담기
            Glide.with(binding.imageView)
                .load(item.imageUrl)
                .into(binding.imageView)
            // 가격
            binding.priceTextView.text = String.format("개당 %d원", item.unitPrice)
            // 제목
            binding.titleTextView.text = item.title
            // 좋아요 개수
            binding.heartScoreTextView.text = likePoint.toString()

            // 좋아요 여부
            if (liked) { // 좋아요가 true 일때
                binding.pickImageView.setImageResource(R.drawable.heart_fill)
            } else { // 좋아요가 false 일때
                binding.pickImageView.setImageResource(R.drawable.heart)
            }

            // 거리
            if (item.walkingDistance > 1000) {
                Log.e("거리 ", item.walkingDistance.toString())
                binding.walkingDistanceTextView.text =
                    String.format("%.1fkm 도보 %d분", item.walkingDistance / 1000, item.walkingTime)
            } else {
                binding.walkingDistanceTextView.text =
                    String.format("%.1fm 도보 %d분", item.walkingDistance, item.walkingTime)
            }

            // 좋아요 눌렀을 때
            binding.pickImageView.setOnClickListener {
                Log.e("liked", liked.toString())
                if (liked) {
                    binding.pickImageView.setImageResource(R.drawable.heart)
                    likePoint -= 1
                    binding.heartScoreTextView.text = likePoint.toString()
                    liked = !liked
                } else {
                    binding.pickImageView.setImageResource(R.drawable.heart_fill)
                    likePoint += 1
                    binding.heartScoreTextView.text = likePoint.toString()
                    liked = !liked
                }
                onLikeClick(item.productId)
            }

            binding.root.setOnClickListener {
                onViewClick(item.productId)
            }
            //binding.walkTimeTextView.text = item.walkingTime.toString()
        }
    }

    inner class HomeMoreViewHolder(val binding: ItemHomeMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: List<ProductsData>) {
            binding.root.setOnClickListener {
                onMoreClick(data)
            }

        }
    }


    companion object {
        const val ITEM = 0
        const val MORE = 1
    }

    override fun getItemCount(): Int {
        val originSize = currentList.size
        return if (originSize == 0) 0 else minOf(currentList.size, 4).inc()
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount - 1 == position) MORE else ITEM
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM -> {
                return HomeViewHolder(
                    ItemHomeProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return HomeMoreViewHolder(
                    ItemHomeMoreBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeViewHolder -> {
                val item = getItem(position)
                holder.bind(item)
            }
            is HomeMoreViewHolder -> {
                val data = currentList
                holder.bind(data)
            }

        }

    }


}