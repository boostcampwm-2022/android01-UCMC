package com.gta.domain.usecase.reservation

import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.usecase.SendNotificationUseCase
import javax.inject.Inject

class FinishReservationUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val notificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(
        accepted: Boolean,
        reservation: Reservation,
        reservationId: String
    ): Boolean {
        val notification = Notification(
            type = if (accepted) NotificationType.ACCEPT_RESERVATION.title else NotificationType.DECLINE_RESERVATION.title,
            message = if (accepted) NotificationType.ACCEPT_RESERVATION.msg else NotificationType.DECLINE_RESERVATION.msg,
            reservationId = reservationId,
            fromId = reservation.ownerId,
            timestamp = System.currentTimeMillis()
        )

        return reservationId.isNotEmpty() && reservationRepository.updateReservationState(
            reservationId,
            if (accepted) ReservationState.ACCEPT else ReservationState.CANCEL
        ) && notificationUseCase(notification, reservation.lenderId)
    }
}
