package com.gta.presentation.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentTransactionListBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TransactionListFragment :
    BaseFragment<FragmentTransactionListBinding>(R.layout.fragment_transaction_list) {
    private val viewModel: TransactionListViewModel by viewModels()
    private val transactionAdapter by lazy { TransactionListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            rvTransactionListTransactions.adapter = transactionAdapter
            srlTransactionListRefresh.setOnRefreshListener {
                transactionAdapter.refresh()
                srlTransactionListRefresh.isRefreshing = false
            }
        }

        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            transactionAdapter.loadStateFlow.collectLatest { state ->
                when (state.append) {
                    is LoadState.Loading -> {
                        binding.cpiTransactionListProgress.isVisible = true
                    }
                    is LoadState.NotLoading -> {
                        binding.cpiTransactionListProgress.isVisible = false
                        binding.tvTransactionListDefaultMsg.isVisible =
                            state.append.endOfPaginationReached && transactionAdapter.itemCount < 1
                    }
                    is LoadState.Error -> {
                        sendSnackBar(resources.getString(R.string.exception_load_data))
                    }
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.transaction.collectLatest {
                transactionAdapter.submitData(it)
            }
        }
    }
}
