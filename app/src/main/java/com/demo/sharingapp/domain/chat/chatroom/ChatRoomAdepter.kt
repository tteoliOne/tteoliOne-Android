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
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ChatRoomAdepter(private val profile: String): ListAdapter<GetChatRoomInfoData,ChatRoomAdepter.ChatRoomViewHolder> (object :DiffUtil.ItemCallback<GetChatRoomInfoData>(){
    override fun areItemsTheSame(oldItem: GetChatRoomInfoData, newItem: GetChatRoomInfoData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: GetChatRoomInfoData, newItem: GetChatRoomInfoData): Boolean {
        return oldItem == newItem
    }
}){
    inner class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item: GetChatRoomInfoData, position: Int){

            val currentTime = changeDate(item.sendDate)
            if (position+1 <= itemCount-1) {
                val beforeTime = changeDate(currentList[position + 1].sendDate)
                if (currentTime.monthValue != beforeTime.monthValue || currentTime.dayOfMonth != beforeTime.dayOfMonth) {
                    binding.dateTextView.visibility = View.VISIBLE
                    binding.dateTextView.text =
                        "${currentTime.year}년 ${currentTime.monthValue}월 ${currentTime.dayOfMonth}일"
                } else {
                    binding.dateTextView.visibility = View.GONE
                }
            }else{
                binding.dateTextView.visibility = View.VISIBLE
                binding.dateTextView.text =
                    "${currentTime.year}년 ${currentTime.monthValue}월 ${currentTime.dayOfMonth}일"
            }

            val hour = currentTime.hour
            val minute = currentTime.minute
            val hourString = if(currentTime.hour < 13) "오전 $hour" else "오후 ${hour-12}"
            binding.timeTextView.text = String.format("%s:%02d ", hourString,minute)
            binding.leftTimeTextView.text = String.format("%s:%02d ", hourString,minute)
            binding.nicknameTextView.text = item.senderName

            if (item.contentType=="notice"){
                binding.dateTextView.visibility = View.VISIBLE
                binding.dateTextView.text = item.content
                binding.chatLayout.visibility= View.GONE
            }

            if (item.mine){ // 자신일때
                binding.layout.gravity= Gravity.END
                binding.profileImageLayout.visibility= View.INVISIBLE
                binding.descriptionTextView.text = item.content
                binding.nicknameTextView.visibility= View.GONE
                binding.timeTextView.visibility = View.GONE
                binding.leftTimeTextView.visibility = View.VISIBLE
                if (item.readCount != 0L){
                    binding.readCountTextView.visibility = View.VISIBLE
                }else{
                    binding.readCountTextView.visibility = View.GONE

                }
            }else{ // 상대방 일때
                binding.leftTimeTextView.visibility = View.GONE
                binding.readCountTextView.visibility = View.GONE
                binding.timeTextView.visibility = View.VISIBLE
                if (position+1 <= itemCount-1){
                    if (currentList[position+1].mine){
                        binding.profileImageLayout.visibility= View.VISIBLE
                        binding.nicknameTextView.visibility= View.VISIBLE

                    }else {
                        binding.profileImageLayout.visibility= View.INVISIBLE
                        binding.nicknameTextView.visibility= View.GONE
                    }
                }else{
                    binding.profileImageLayout.visibility= View.VISIBLE
                    binding.nicknameTextView.visibility= View.VISIBLE

                }
                binding.layout.gravity= Gravity.START
                Glide.with(binding.profileImageView)
                    .load(profile)
                    .circleCrop()
                    .into(binding.profileImageView)
                binding.descriptionTextView.text = item.content
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(currentList[position],position)
    }

    fun changeDate(date: Long):ZonedDateTime{
        val instant = Instant.ofEpochMilli(date)
        // Instant을 특정 타임존(예: 시스템 디폴트)으로 변환
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())

    }
}