package com.demo.sharingapp.domain.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.databinding.ItemOtherReviewBinding
import com.demo.sharingapp.domain.review.data.ReviewResponseData

class ReviewAdepter: ListAdapter<ReviewResponseData, ReviewAdepter.ReviewViewHolder>(object :DiffUtil.ItemCallback<ReviewResponseData>(){
    override fun areItemsTheSame(
        oldItem: ReviewResponseData,
        newItem: ReviewResponseData,
    ): Boolean {
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(
        oldItem: ReviewResponseData,
        newItem: ReviewResponseData,
    ): Boolean {
        return oldItem == newItem
    }
}){
    inner class ReviewViewHolder(private val binding: ItemOtherReviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ReviewResponseData){
            binding.nicknameTextView.text = item.writer
            binding.descriptionTextView.text = item.content
            binding.goodCountTextView.text = item.ddabongScore.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(ItemOtherReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}