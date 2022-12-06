package com.gta.domain.repository

import com.gta.domain.model.SimpleReservation

interface TransactionRepository {
    suspend fun getYourCarTransactions(uid: String): List<SimpleReservation>
    suspend fun getMyCarTransactions(uid: String): List<SimpleReservation>
}
