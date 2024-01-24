package com.demo.sharingapp.domain.chat.chatroom

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemChatRoomBinding
import com.demo.sharingapp.domain.chat.chatroom.data.ChatRoomData

class ChatRoomAdepter: ListAdapter<ChatRoomData,ChatRoomAdepter.ChatRoomViewHolder> (object :DiffUtil.ItemCallback<ChatRoomData>(){
    override fun areItemsTheSame(oldItem: ChatRoomData, newItem: ChatRoomData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ChatRoomData, newItem: ChatRoomData): Boolean {
        return oldItem == newItem
    }
}){
    inner class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChatRoomData){
            if (item.chatType == 1){ // 상대방
                binding.profileImageView.isVisible= true
                binding.layout.gravity= Gravity.START
                Glide.with(binding.profileImageView)
                    .load(item.profile)
                    .circleCrop()
                    .into(binding.profileImageView)
                binding.descriptionTextView.text = item.description
            }else{
                binding.layout.gravity= Gravity.END
                binding.profileImageView.isVisible= false
                binding.descriptionTextView.text = item.description
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}