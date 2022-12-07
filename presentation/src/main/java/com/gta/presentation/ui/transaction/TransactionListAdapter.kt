package com.gta.presentation.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gta.domain.model.Transaction
import com.gta.presentation.R
import com.gta.presentation.databinding.ItemTransactionBinding

class TransactionListAdapter : ListAdapter<Transaction, TransactionListAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {
            with(binding) {
                this.item = item
                root.setOnClickListener {
                    TransactionFragment.navigateToReservationCheck(it, item.reservationId)
                }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_transaction,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.reservationId == newItem.reservationId
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}
