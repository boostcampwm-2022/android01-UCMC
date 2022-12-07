package com.gta.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gta.data.source.TransactionDataSource
import com.gta.data.source.TransactionPagingSource
import com.gta.domain.model.SimpleReservation
import com.gta.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(private val transactionDataSource: TransactionDataSource) : TransactionRepository {
    override fun getTransactions(userId: String, isLender: Boolean): Flow<PagingData<SimpleReservation>> {
        return Pager(PagingConfig(10)) {
            TransactionPagingSource(userId, isLender, transactionDataSource)
        }.flow
    }
}
