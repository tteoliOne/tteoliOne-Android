package com.demo.sharingapp.domain.home.part

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.demo.sharingapp.databinding.ItemDetailedImageBinding
import com.demo.sharingapp.domain.home.part.data.DetailedImageData

class FrameAdapter(private val list: List<DetailedImageData>):RecyclerView.Adapter<FrameAdapter.FrameViewHolder>()  {
    inner class FrameViewHolder(private val binding: ItemDetailedImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: DetailedImageData) {
            Glide.with(binding.frameImageView)
                .load(item.uri)
                .into(binding.frameImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemDetailedImageBinding.inflate(inflater, parent, false)
        return FrameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}