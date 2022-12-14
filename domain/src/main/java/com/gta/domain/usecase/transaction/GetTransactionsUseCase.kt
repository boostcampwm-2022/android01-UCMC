package com.gta.domain.usecase.transaction

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.gta.domain.model.Transaction
import com.gta.domain.model.toTransaction
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val carRepository: CarRepository,
    private val transactionRepository: TransactionRepository
) {
    private val tradingCondition =
        { transaction: Transaction -> transaction.reservationState.state >= 0 }
    private val completedCondition =
        { transaction: Transaction -> transaction.reservationState.state < 0 }

    operator fun invoke(
        uid: String,
        isLender: Boolean,
        isTrading: Boolean
    ): Flow<PagingData<Transaction>> {
        val transactionPagingData = transactionRepository.getTransactions(uid, isLender)

        val filterCondition = if (isTrading) tradingCondition else completedCondition

        return transactionPagingData.map { pagingData ->
            pagingData.map { reservation ->
                val simpleCar = carRepository.getSimpleCar(reservation.carId).first()
                reservation.toTransaction(simpleCar)
            }.filter(filterCondition)
        }
    }
}
