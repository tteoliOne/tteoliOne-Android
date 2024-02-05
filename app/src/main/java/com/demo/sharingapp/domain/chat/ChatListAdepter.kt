package com.demo.sharingapp.domain.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemChatListBinding
import com.demo.sharingapp.domain.chat.data.ChatListData
import com.demo.sharingapp.domain.chat.data.GetChatListData

class ChatListAdepter(val onClick:(item:GetChatListData) -> Unit): ListAdapter<GetChatListData,ChatListAdepter.ChatListViewHolder>(object : DiffUtil.ItemCallback<GetChatListData>(){
    override fun areItemsTheSame(oldItem: GetChatListData, newItem: GetChatListData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: GetChatListData, newItem: GetChatListData): Boolean {
        return oldItem == newItem
    }
}) {
    inner class ChatListViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:GetChatListData){

            binding.nicknameTextView.text = item.participant.username
            if (item.latestMessage != null){
                binding.descriptionTextView.text = item.latestMessage.context
            }

            binding.titleTextView.text = item.productTitle
            Glide.with(binding.profileImageView)
                .load(item.participant.profile)
                .circleCrop()
                .into(binding.profileImageView)

            binding.root.setOnClickListener {
                onClick(item)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}