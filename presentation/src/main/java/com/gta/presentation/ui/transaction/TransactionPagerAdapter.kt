package com.gta.presentation.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gta.presentation.model.TransactionState

class TransactionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val transactionStates = TransactionState.values()

    override fun getItemCount(): Int = transactionStates.size

    override fun createFragment(position: Int): Fragment {
        val fragment = TransactionListFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_KEY, transactionStates[position])
        }
        return fragment
    }

    companion object {
        const val ARG_KEY = "TRANSACTION_STATE"
    }
}