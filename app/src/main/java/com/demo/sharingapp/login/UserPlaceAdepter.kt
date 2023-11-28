package com.demo.sharingapp.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.databinding.ItemAddressCellBinding
import com.demo.sharingapp.databinding.ItemHomeProductBinding
import com.demo.sharingapp.login.data.Juso

class UserPlaceAdepter(val onItemClicked:(String)->Unit):ListAdapter<Juso, UserPlaceAdepter.UserPlaceViewHolder>(object : DiffUtil.ItemCallback<Juso>(){
    override fun areItemsTheSame(oldItem: Juso, newItem: Juso): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Juso, newItem: Juso): Boolean {
        return oldItem == newItem
    }
}) {
    inner class UserPlaceViewHolder(val binding: ItemAddressCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:Juso){
            binding.roadAddressTextView.text = item.roadAddrPart1
            binding.jiBunAddressTextView.text = item.jibunAddr
            binding.root.setOnClickListener {
                onItemClicked(item.roadAddrPart1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPlaceViewHolder {
        return UserPlaceViewHolder(
            ItemAddressCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserPlaceViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}