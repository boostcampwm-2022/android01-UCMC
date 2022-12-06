package com.gta.domain.usecase.transaction

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
    operator fun invoke(uid: String, isLender: Boolean): Flow<List<Transaction>> {
        val transactions = if (isLender) {
            transactionRepository.getYourCarTransactions(uid)
        } else {
            transactionRepository.getMyCarTransactions(uid)
        }

        return transactions.map { reservations ->
            reservations.map {
                val simpleCar = carRepository.getSimpleCar(it.carId).first()
                it.toTransaction(simpleCar)
            }
        }
    }
}
