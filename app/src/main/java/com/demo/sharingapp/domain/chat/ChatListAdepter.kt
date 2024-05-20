package com.demo.sharingapp.domain.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemChatListBinding
import com.demo.sharingapp.domain.chat.data.ChatListData
import com.demo.sharingapp.domain.chat.data.GetChatListData
import java.time.Duration
import java.time.Instant

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
                val time = item.latestMessage.sendAt
                val sendTime = Instant.ofEpochMilli(time)
                val currentTime = Instant.now()
                val duration = Duration.between(sendTime, currentTime)
                val timeAgo = calculateTimeAgo(duration)
                binding.timeTextView.text = timeAgo

                if (item.unReadCount != 0L){
                    binding.unReadTextView.text = item.unReadCount.toString()
                    binding.unReadLayout.isVisible = true
                }else{
                    binding.unReadLayout.isVisible = false
                }
            }else{
                binding.descriptionTextView.text = ""
                binding.timeTextView.text = ""
                binding.unReadLayout.isVisible = false
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

    fun calculateTimeAgo(duration: Duration): String{
        val seconds = duration.seconds
        if (seconds < 60){
            return seconds.toString() + "초 전"
        }else {
            val minutes = seconds / 60
            return if (minutes < 60){
                minutes.toString() + "분 전"
            }else {
                val hours = minutes / 60
                if (hours < 24){
                    hours.toString() + "시간 전"
                } else{
                    val day = hours / 24
                    day.toString() + "일 전"
                }
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