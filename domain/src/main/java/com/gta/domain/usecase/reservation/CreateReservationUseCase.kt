package com.gta.domain.usecase.reservation

import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.usecase.SendNotificationUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(
    private val repository: ReservationRepository,
    private val notificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(reservation: Reservation, ownerId: String): Boolean {
        val reservationId = repository.createReservation(reservation).first()
        val notificationResult = notificationUseCase(
            Notification(
                type = NotificationType.REQUEST_RESERVATION.title,
                message = RESERVATION_REQUEST_MESSAGE,
                reservationId = reservationId,
                fromId = reservation.userId
            ),
            ownerId
        )
        return reservationId.isNotEmpty() && notificationResult
    }

    companion object {
        private const val RESERVATION_REQUEST_MESSAGE = "자동차 대여 예약 요청이 도착했습니다."
    }
}
