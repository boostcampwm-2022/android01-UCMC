package com.gta.data.repository

import com.gta.data.model.toProfile
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.ReviewDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DuplicatedReview
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.ReviewType
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    private val carDataSource: CarDataSource,
    private val reservationDataSource: ReservationDataSource,
    private val userDataSource: UserDataSource
) : ReviewRepository {
    override suspend fun addReview(
        opponentId: String,
        reservationId: String,
        review: UserReview
    ): UCMCResult<Unit> {
        /*
            1. 상대방 정보를 불러온다.
            2. 리뷰를 저장한다.
            3. 온도를 갱신한다.
         */
        return userDataSource.getUser(opponentId).first()?.let { user ->
            val addReviewResult = reviewDataSource.addReview(opponentId, reservationId, review).first()
            val updatedTemperature = user.temperature + calcTemperature(review.rating)
            val updateTemperatureResult = reviewDataSource.updateTemperature(opponentId, updatedTemperature).first()
            if (addReviewResult && updateTemperatureResult) {
                UCMCResult.Success(Unit)
            } else {
                UCMCResult.Error(FirestoreException())
            }
        } ?: UCMCResult.Error(FirestoreException())
    }

    override suspend fun getReviewDTO(uid: String, reservationId: String): UCMCResult<ReviewDTO> {
        /*
            예약 정보 받아오기
            1. 예약 id로 예약을 불러온다.
            2. 예약에 있는 차 아이디로 차 정보를 불러온다. (carId, carModel 얻기)
            3. 예약의 lenderId와 uid를 비교해서 리뷰를 보내는 대상을 특정한다.
            3-1. 같으면 대여자 -> 차주에게 리뷰를 보내는 케이스 (차 정보의 ownerId에서 UserProfile 얻기)
            3-2. 다르면 차주 -> 대여자에게 리뷰를 보내는 케이스 (예약의 lenderId에서 UserProfile 얻기)
         */
        return reservationDataSource.getReservation(reservationId).first()?.let { reservation ->
            val reviewType =
                if (reservation.lenderId == uid) ReviewType.LENDER_TO_OWNER else ReviewType.OWNER_TO_LENDER
            val opponentId =
                if (reservation.lenderId == uid) reservation.ownerId else reservation.lenderId
            // 중복된 리뷰 등록을 시도하면 에러 반환
            if (isDuplicatedReview(opponentId, reservationId)) {
                return UCMCResult.Error(DuplicatedReview())
            }
            carDataSource.getCar(reservation.carId).first()?.let { car ->
                val carImage = if (car.images.isNotEmpty()) car.images[0] else ""
                userDataSource.getUser(opponentId).first()?.let { userInfo ->
                    val profile = userInfo.toProfile(opponentId)
                    UCMCResult.Success(
                        ReviewDTO(
                            reviewType = reviewType,
                            opponent = profile,
                            carImage = carImage,
                            carModel = car.pinkSlip.model
                        )
                    )
                } ?: UCMCResult.Error(FirestoreException())
            } ?: UCMCResult.Error(FirestoreException())
        } ?: UCMCResult.Error(FirestoreException())
    }

    private suspend fun isDuplicatedReview(opponentId: String, reservationId: String) =
        reviewDataSource.isExistReview(opponentId, reservationId).first()


    private fun calcTemperature(temperature: Float): Float = temperature - MIDDLE_RATING

    companion object {
        private const val MIDDLE_RATING = 3.0f
    }
}
