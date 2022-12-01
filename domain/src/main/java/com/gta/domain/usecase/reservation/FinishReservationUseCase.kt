package com.gta.domain.usecase.reservation

import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.usecase.SendNotificationUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FinishReservationUseCase @Inject constructor(
    private val repository: ReservationRepository,
    private val notificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(
        accepted: Boolean,
        reservation: Reservation,
        ownerId: String
    ): Boolean {
        val reservationId = repository.createReservation(reservation).first()
        val notificationResult = notificationUseCase(
            Notification(
                type = if (accepted) NotificationType.ACCEPT_RESERVATION.title else NotificationType.DECLINE_RESERVATION.title,
                message = if (accepted) RESERVATION_ACCEPT_MESSAGE else RESERVATION_DECLINE_MESSAGE,
                reservationId = reservationId,
                carId = reservation.carId,
                fromId = ownerId
            ),
            reservation.lenderId
        )
        return reservationId.isNotEmpty() && notificationResult
    }

    companion object {
        private const val RESERVATION_ACCEPT_MESSAGE = "자동차 대여 예약이 완료되었습니다."
        private const val RESERVATION_DECLINE_MESSAGE = "자동차 대여 예약이 거절되었습니다."
    }
}
