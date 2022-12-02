package com.gta.presentation.ui.cardetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gta.presentation.R
import com.gta.presentation.databinding.ItemCarDetailImageBinding

class CarImagePagerAdapter :
    ListAdapter<String, CarImagePagerAdapter.ImageViewHolder>(ImagesDiffCallback()) {

    class ImageViewHolder(
        private val binding: ItemCarDetailImageBinding
    ) : ViewHolder(binding.root) {
        fun bind(img: String) {
            binding.img = img
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_car_detail_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class ImagesDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
