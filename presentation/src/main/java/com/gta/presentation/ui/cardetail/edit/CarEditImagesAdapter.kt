package com.gta.presentation.ui.cardetail.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gta.presentation.R
import com.gta.presentation.databinding.ItemCarDetailEditImageBinding
import com.gta.presentation.ui.GlideApp
import com.gta.presentation.ui.cardetail.edit.CarEditImagesAdapter.ImageViewHolder

class CarEditImagesAdapter : ListAdapter<String, ImageViewHolder>(ImagesDiffCallback()) {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }

    class ImageViewHolder(
        private val binding: ItemCarDetailEditImageBinding,
        itemClickListener: OnItemClickListener?
    ) : ViewHolder(binding.root) {

        init {
            binding.ivDelete.setOnClickListener {
                itemClickListener?.onClick(bindingAdapterPosition)
            }
        }

        fun bind(img: String) {
            GlideApp.with(binding.ivCar.context)
                .load(img)
                .into(binding.ivCar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_car_detail_edit_image,
                parent,
                false
            ),
            itemClickListener
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