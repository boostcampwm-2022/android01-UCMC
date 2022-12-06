package com.gta.presentation.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentTransactionBinding
import com.gta.presentation.model.TransactionState
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment

class TransactionFragment : BaseFragment<FragmentTransactionBinding>(R.layout.fragment_transaction) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: TransactionFragmentArgs by navArgs()
        (requireActivity() as MainActivity).supportActionBar?.title = args.title

        binding.vpTransactionListFragment.adapter = TransactionPagerAdapter(this)
        setUpTabLayoutWithViewPager()
    }

    private fun setUpTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tlTransactionListTabs, binding.vpTransactionListFragment) { tab, position ->
            tab.text = TransactionState.values()[position].state
        }.attach()
    }
}