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
import com.demo.sharingapp.domain.chat.chatroom.data.GetChatRoomInfoData

class ChatRoomAdepter(private val profile: String): ListAdapter<GetChatRoomInfoData,ChatRoomAdepter.ChatRoomViewHolder> (object :DiffUtil.ItemCallback<GetChatRoomInfoData>(){
    override fun areItemsTheSame(oldItem: GetChatRoomInfoData, newItem: GetChatRoomInfoData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: GetChatRoomInfoData, newItem: GetChatRoomInfoData): Boolean {
        return oldItem == newItem
    }
}){
    inner class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item: GetChatRoomInfoData){
            binding.root.setOnClickListener{

            }
            if (item.mine){ // 자신일때
                binding.layout.gravity= Gravity.END
                binding.profileImageLayout.visibility= View.INVISIBLE
                binding.descriptionTextView.text = item.content
            }else{ // 상대방 일때
                binding.profileImageLayout.visibility= View.VISIBLE
                binding.layout.gravity= Gravity.START
                Glide.with(binding.profileImageView)
                    .load(profile)
                    .circleCrop()
                    .into(binding.profileImageView)
                binding.descriptionTextView.text = item.content
            }

//            if (item.mine){ // 자신일때
//                binding.profileImageView.isVisible= true
//                binding.layout.gravity= Gravity.START
//                Glide.with(binding.profileImageView)
//                    .load(item.profile)
//                    .circleCrop()
//                    .into(binding.profileImageView)
//                binding.descriptionTextView.text = item.description
//            }else{ // 상대방 일때
//                binding.layout.gravity= Gravity.END
//                binding.profileImageView.isVisible= false
//                binding.descriptionTextView.text = item.description
//            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}