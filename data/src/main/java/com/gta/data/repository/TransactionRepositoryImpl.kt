package com.gta.data.repository

import com.gta.data.source.TransactionDataSource
import com.gta.domain.model.SimpleReservation
import com.gta.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(private val transactionDataSource: TransactionDataSource): TransactionRepository {
    override suspend fun getYourCarTransactions(uid: String): List<SimpleReservation> {
        return transactionDataSource.getYourCarTransactions(uid).first()
    }

    override suspend fun getMyCarTransactions(uid: String): List<SimpleReservation> {
        return transactionDataSource.getMyCarTransactions(uid).first()
    }
}
