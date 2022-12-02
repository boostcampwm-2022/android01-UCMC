package com.gta.domain.usecase.returncar

import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.ReservationState
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.usecase.SendNotificationUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReturnCarUseCase @Inject constructor(
    private val carRepository: CarRepository,
    private val reservationRepository: ReservationRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(reservationId: String, carId: String, userId: String): Boolean {
        val ownerId = carRepository.getOwnerId(carId).first()
        val notification = Notification(
            type = NotificationType.RETURN_CAR.title,
            message = NotificationType.RETURN_CAR.msg,
            reservationId = reservationId,
            fromId = userId
        )
        return ownerId.isNotEmpty() && reservationRepository.updateReservationState(
            reservationId,
            ReservationState.DONE
        ) && sendNotificationUseCase(notification, ownerId)
    }
}
