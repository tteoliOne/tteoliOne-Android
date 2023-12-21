package com.demo.sharingapp.domain.home.part

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemDetailedFrameImageBinding
import com.demo.sharingapp.databinding.ItemDetailedImageBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData


class DetailedFrameAdapter(private val list: List<DetailedImageData>):
    RecyclerView.Adapter<DetailedFrameAdapter.DetailedFrameViewHolder>()  {
    inner class DetailedFrameViewHolder(private val binding: ItemDetailedFrameImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: DetailedImageData) {
            Glide.with(binding.detailedFrameImageView)
                .load(item.uri)
                .into(binding.detailedFrameImageView)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailedFrameViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemDetailedFrameImageBinding.inflate(inflater, parent, false)
        return DetailedFrameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailedFrameViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}