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
                message = if (accepted) NotificationType.ACCEPT_RESERVATION.msg else NotificationType.DECLINE_RESERVATION.msg,
                reservationId = reservationId,
                fromId = ownerId
            ),
            reservation.lenderId
        )
        return reservationId.isNotEmpty() && notificationResult
    }
}
