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

class ChatListAdepter(val onClick:() -> Unit): ListAdapter<ChatListData,ChatListAdepter.ChatListViewHolder>(object : DiffUtil.ItemCallback<ChatListData>(){
    override fun areItemsTheSame(oldItem: ChatListData, newItem: ChatListData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ChatListData, newItem: ChatListData): Boolean {
        return oldItem == newItem
    }
}) {
    inner class ChatListViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:ChatListData){
            binding.nicknameTextView.text = item.nickname
            binding.descriptionTextView.text = item.description
            binding.titleTextView.text = item.title
            Glide.with(binding.profileImageView)
                .load(item.profile)
                .circleCrop()
                .into(binding.profileImageView)

            binding.root.setOnClickListener {
                onClick()
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