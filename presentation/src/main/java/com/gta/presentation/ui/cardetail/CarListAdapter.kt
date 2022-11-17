package com.gta.presentation.ui.cardetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gta.domain.model.SimpleCar
import com.gta.presentation.R
import com.gta.presentation.databinding.ItemOwnerCarBinding

class CarListAdapter : ListAdapter<SimpleCar, CarListAdapter.CarViewHolder>(CarListDiffCallback()) {

    class CarViewHolder(
        private val binding: ItemOwnerCarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SimpleCar) {
            with(binding) {
                this.item = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        return CarViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_owner_car,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class CarListDiffCallback : DiffUtil.ItemCallback<SimpleCar>() {
    override fun areItemsTheSame(oldItem: SimpleCar, newItem: SimpleCar): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SimpleCar, newItem: SimpleCar): Boolean {
        return oldItem == newItem
    }
}
