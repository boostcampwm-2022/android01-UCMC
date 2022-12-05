package com.gta.data.repository

import com.gta.data.source.TransactionDataSource
import com.gta.domain.model.SimpleReservation
import com.gta.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(private val transactionDataSource: TransactionDataSource): TransactionRepository {
    override fun getYourCarTransactions(uid: String): Flow<List<SimpleReservation>> {
        return transactionDataSource.getYourCarTransactions(uid)
    }

    override fun getMyCarTransactions(uid: String): Flow<List<SimpleReservation>> {
        return transactionDataSource.getMyCarTransactions(uid)
    }
}
