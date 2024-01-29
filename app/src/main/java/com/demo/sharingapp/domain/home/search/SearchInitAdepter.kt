package com.demo.sharingapp.domain.home.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.databinding.ItemSearchInitBinding
import com.demo.sharingapp.databinding.ItemSearchInputBinding
import com.demo.sharingapp.domain.home.search.data.SearchInitData

class SearchInitAdepter(private val onTitle:(String)->Unit, private val onRemove:(String)->Unit): ListAdapter<SearchInitData, SearchInitAdepter.SearchInitViewHolder>(object :DiffUtil.ItemCallback<SearchInitData>(){
    override fun areItemsTheSame(oldItem: SearchInitData, newItem: SearchInitData): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: SearchInitData, newItem: SearchInitData): Boolean {
        return oldItem == newItem
    }
}) {
    inner class SearchInitViewHolder(private val binding: ItemSearchInitBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SearchInitData){
            binding.searchRecodeTextView.text = item.searchData
            binding.removeButton.setOnClickListener {
                onRemove(item.searchData)
            }
            binding.searchRecodeTextView.setOnClickListener {
                onTitle(item.searchData)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchInitViewHolder {
        return SearchInitViewHolder(ItemSearchInitBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchInitViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}