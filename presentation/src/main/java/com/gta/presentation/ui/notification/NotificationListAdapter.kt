package com.gta.presentation.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gta.domain.model.NotificationInfo
import com.gta.domain.model.NotificationType
import com.gta.presentation.R
import com.gta.presentation.databinding.ItemNotificationListBinding

class NotificationListAdapter :
    PagingDataAdapter<NotificationInfo, NotificationListAdapter.NotificationViewHolder>(
        NotificationDiffCallback()
    ) {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(type: NotificationType, reservation: String)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationListBinding,
        itemClickListener: OnItemClickListener?
    ) : ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                binding.notification?.let {
                    itemClickListener?.onClick(it.type, it.reservationId)
                }
            }
        }
        fun bind(item: NotificationInfo) {
            binding.notification = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_notification_list,
                parent,
                false
            ),
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

private class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationInfo>() {
    override fun areItemsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotificationInfo, newItem: NotificationInfo): Boolean {
        return oldItem == newItem
    }
}
