package com.demo.sharingapp.domain.home.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.sharingapp.databinding.ItemSearchInputBinding
import com.demo.sharingapp.domain.home.data.PartProductContent

class SearchInputAdepter(private val onClick:(String)-> Unit): ListAdapter<PartProductContent, SearchInputAdepter.SearchInputViewHolder>(object :DiffUtil.ItemCallback<PartProductContent>(){
    override fun areItemsTheSame(
        oldItem: PartProductContent,
        newItem: PartProductContent,
    ): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(
        oldItem: PartProductContent,
        newItem: PartProductContent,
    ): Boolean {
        return oldItem == newItem
    }
}) {
    inner class SearchInputViewHolder(private val binding: ItemSearchInputBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: PartProductContent){
            binding.searchTextView.text = item.title
            binding.root.setOnClickListener {
                onClick(item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInputViewHolder {
        return SearchInputViewHolder(ItemSearchInputBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchInputViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}