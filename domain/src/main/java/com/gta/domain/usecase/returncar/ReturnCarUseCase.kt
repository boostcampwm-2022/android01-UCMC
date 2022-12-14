package com.gta.domain.usecase.returncar

import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationType
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
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
    suspend operator fun invoke(reservationId: String, carId: String, userId: String): UCMCResult<Unit> {
        val userResult = carRepository.getOwnerId(carId).first()
        val notification = Notification(
            type = NotificationType.RETURN_CAR.title,
            message = NotificationType.RETURN_CAR.msg,
            reservationId = reservationId,
            fromId = userId,
            timestamp = System.currentTimeMillis()
        )

        // userId가 있고 -> 예약 상태 업데이트 성공하고 -> Notification 보내기
        return if (userResult is UCMCResult.Success) {
            val updateResult = reservationRepository.updateReservationState(reservationId, ReservationState.DONE)
            return if (updateResult is UCMCResult.Success) {
                sendNotificationUseCase(notification, userResult.data)
                // TODO: 트랜잭션 처리
            } else {
                updateResult
            }
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
