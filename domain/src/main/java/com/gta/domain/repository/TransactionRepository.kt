package com.gta.domain.repository

import androidx.paging.PagingData
import com.gta.domain.model.SimpleReservation
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(userId: String, isLender: Boolean): Flow<PagingData<SimpleReservation>>
}
