package com.gta.presentation.ui.transaction

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.Transaction
import com.gta.domain.usecase.transaction.GetTransactionsUseCase
import com.gta.presentation.R
import com.gta.presentation.model.TransactionUserState
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    args: SavedStateHandle,
    getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    val transaction: StateFlow<List<Transaction>>

    init {
        val userState = args.get<TransactionUserState>(TransactionPagerAdapter.USER_STATE_ARG) ?: TransactionUserState.LENDER

        transaction = getTransactionsUseCase(
            FirebaseUtil.uid,
            userState == TransactionUserState.LENDER
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}