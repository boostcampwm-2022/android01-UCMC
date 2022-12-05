package com.gta.domain.repository

import com.gta.domain.model.SimpleReservation
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getYourCarTransactions(uid: String): Flow<List<SimpleReservation>>
    fun getMyCarTransactions(uid: String): Flow<List<SimpleReservation>>
}
