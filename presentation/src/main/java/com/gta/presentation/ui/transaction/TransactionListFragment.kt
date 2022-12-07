package com.gta.presentation.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentTransactionListBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionListFragment : BaseFragment<FragmentTransactionListBinding>(R.layout.fragment_transaction_list) {
    private val viewModel: TransactionListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            vm = viewModel
            rvTransactionListTransactions.adapter = TransactionListAdapter()
            srlTransactionListRefresh.setOnRefreshListener {
                srlTransactionListRefresh.isRefreshing = false
                viewModel.setTransactions()
            }
        }
    }
}
