package com.gta.presentation.ui.mypage.mycars

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gta.domain.model.SimpleCar
import com.gta.presentation.databinding.ItemMypageCarlistBinding

class MyCarsListAdapter :
    ListAdapter<SimpleCar, MyCarsListAdapter.CarViewHolder>(diffUtil) {
    class CarViewHolder(
        private val binding: ItemMypageCarlistBinding,
        private val listener: OnItemClickListener<String>?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: SimpleCar) {
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

    private var listener: OnItemClickListener<String>? = null

    fun setOnItemClickListener(listener: OnItemClickListener<String>) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding =
            ItemMypageCarlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SimpleCar>() {
            override fun areItemsTheSame(
                oldItem: SimpleCar,
                newItem: SimpleCar
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SimpleCar,
                newItem: SimpleCar
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
