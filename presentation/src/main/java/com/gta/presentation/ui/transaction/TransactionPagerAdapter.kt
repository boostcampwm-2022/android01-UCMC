package com.gta.presentation.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gta.presentation.model.TransactionState
import com.gta.presentation.model.TransactionUserState

class TransactionPagerAdapter(private val userState: TransactionUserState, fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val transactionStates = TransactionState.values()

    override fun getItemCount(): Int = transactionStates.size

    override fun createFragment(position: Int): Fragment {
        val fragment = TransactionListFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(USER_STATE_ARG, userState)
            putParcelable(TRANSACTION_STATE_ARG, transactionStates[position])
        }
        return fragment
    }

    companion object {
        const val TRANSACTION_STATE_ARG = "TRANSACTION_STATE"
        const val USER_STATE_ARG = "USER_STATE"
    }
}