package com.gta.presentation.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.Transaction
import com.gta.domain.usecase.transaction.GetTransactionsUseCase
import com.gta.presentation.model.TransactionState
import com.gta.presentation.model.TransactionUserState
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    args: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val userState: TransactionUserState
    private val transactionState: TransactionState

    private val _transaction = MutableStateFlow<List<Transaction>>(emptyList())
    val transaction: StateFlow<List<Transaction>> get() = _transaction

    init {
        userState = args.get<TransactionUserState>(TransactionPagerAdapter.USER_STATE_ARG)
            ?: TransactionUserState.LENDER
        transactionState = args.get<TransactionState>(TransactionPagerAdapter.TRANSACTION_STATE_ARG)
            ?: TransactionState.TRADING

        setTransactions()
    }

    fun setTransactions() {
        viewModelScope.launch {
            _transaction.emit(
                getTransactionsUseCase(
                    FirebaseUtil.uid,
                    userState == TransactionUserState.LENDER,
                    transactionState == TransactionState.TRADING
                )
            )
        }
    }
}
