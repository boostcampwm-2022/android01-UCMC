package com.gta.domain.usecase.reservation

import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.Reservation
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.usecase.SendNotificationUseCase
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val repository: ReservationRepository,
    private val notificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(reservation: Reservation): UCMCResult<Unit> {
        return when (val result = repository.createReservation(reservation)) {
            is UCMCResult.Success -> {
                return notificationUseCase(
                    Notification(
                        type = NotificationType.REQUEST_RESERVATION.title,
                        message = NotificationType.REQUEST_RESERVATION.msg,
                        reservationId = result.data,
                        fromId = reservation.lenderId,
                        timestamp = System.currentTimeMillis()
                    ),
                    reservation.ownerId
                )
            }
            is UCMCResult.Error -> {
                result
            }
        }
    }
}
