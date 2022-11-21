package com.gta.presentation.ui.mypage.mycars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gta.presentation.databinding.ItemMypageCarlistBinding
import com.gta.presentation.model.carDetail.CarInfo

class MyCarsListAdapter() :
    ListAdapter<CarInfo, MyCarsListAdapter.CarViewHolder>(diffUtil) {
    inner class CarViewHolder(private val binding: ItemMypageCarlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: CarInfo) {
            binding.data = info
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onClick(info.id)
                }
            }
            binding.root.setOnLongClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onLongClick(binding.root, info.id)
                }
                return@setOnLongClickListener true
            }
        }
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding =
            ItemMypageCarlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CarInfo>() {
            override fun areItemsTheSame(
                oldItem: CarInfo,
                newItem: CarInfo
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CarInfo,
                newItem: CarInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
