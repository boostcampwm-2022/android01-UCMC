package com.gta.domain.usecase.transaction

import com.gta.domain.model.Transaction
import com.gta.domain.model.toTransaction
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val carRepository: CarRepository,
    private val transactionRepository: TransactionRepository
) {
    private val tradingCondition =
        { transaction: Transaction -> transaction.reservationState.state >= 0 }
    private val completedCondition =
        { transaction: Transaction -> transaction.reservationState.state < 0 }

    suspend operator fun invoke(
        uid: String,
        isLender: Boolean,
        isTrading: Boolean
    ): List<Transaction> {
        val transactions = if (isLender) {
            transactionRepository.getYourCarTransactions(uid)
        } else {
            transactionRepository.getMyCarTransactions(uid)
        }

        return transactions.map { reservation ->
            val simpleCar = carRepository.getSimpleCar(reservation.carId).first()
            reservation.toTransaction(simpleCar)
        }.filter(if (isTrading) tradingCondition else completedCondition)
    }
}
