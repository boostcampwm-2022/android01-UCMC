package com.gta.data.repository

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.model.toProfile
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.ReviewDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.ReviewType
import com.gta.domain.model.UserProfile
import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    private val carDataSource: CarDataSource,
    private val reservationDataSource: ReservationDataSource,
    private val userDataSource: UserDataSource
) : ReviewRepository {
    override fun addReview(
        opponentId: String,
        reservationId: String,
        review: UserReview
    ): Flow<Boolean> = callbackFlow {
        /*
            1. 리뷰를 저장한다.
            2. 상대방 정보를 불러온다.
            3. 온도를 갱신한다.
         */
        reviewDataSource.addReview(opponentId, reservationId, review).addOnSuccessListener {
            userDataSource.getUser(opponentId).addOnSuccessListener { userSnapshot ->
                userSnapshot.toObject(UserInfo::class.java)?.let { userInfo ->
                    val updatedTemperature = userInfo.temperature + calcTemperature(review.rating)
                    reviewDataSource.updateTemperature(opponentId, updatedTemperature).addOnCompleteListener {
                        trySend(it.isSuccessful)
                    }
                } ?: trySend(false)
            }.addOnFailureListener {
                trySend(false)
            }
        }.addOnFailureListener {
            trySend(false)
        }
        awaitClose()
    }

    override fun getReviewDTO(uid: String, reservationId: String): Flow<ReviewDTO> = callbackFlow {
        /*
            1. 예약 id로 예약을 불러온다.
            2. 예약에 있는 차 아이디로 차 정보를 불러온다. (carId, carModel 얻기)
            3. 예약의 userId와 uid를 비교해서 리뷰를 보내는 대상을 특정한다.
            3-1. 같으면 대여자 -> 차주에게 리뷰를 보내는 케이스 (차 정보의 ownerId에서 UserProfile 얻기)
            3-2. 다르면 차주 -> 대여자에게 리뷰를 보내는 케이스 (예약의 userId에서 UserProfile 얻기)
         */
        reservationDataSource.getReservation(reservationId).addOnSuccessListener { reservationSnapshot ->
            val reservation = reservationSnapshot.toObject(Reservation::class.java) ?: Reservation()
            val reviewType = if (reservation.userId == uid) ReviewType.LENDER_TO_OWNER else ReviewType.OWNER_TO_LENDER
            carDataSource.getCar(reservation.carId).addOnSuccessListener { carSnapshot ->
                val car = carSnapshot.toObject(Car::class.java) ?: Car()
                val carImage = if (car.images.isNotEmpty()) car.images[0] else ""
                val opponentId = if (reviewType == ReviewType.LENDER_TO_OWNER) car.ownerId else reservation.userId
                userDataSource.getUser(opponentId).addOnSuccessListener { userSnapshot ->
                    val profile = userSnapshot?.toObject(UserInfo::class.java)?.toProfile(opponentId) ?: UserProfile()
                    trySend(
                        ReviewDTO(
                            reviewType = reviewType,
                            opponent = profile,
                            carImage = carImage,
                            carModel = car.pinkSlip.model
                        )
                    )
                }.addOnFailureListener {
                    trySend(ReviewDTO())
                }
            }.addOnFailureListener {
                trySend(ReviewDTO())
            }
        }.addOnFailureListener {
            trySend(ReviewDTO())
        }
        awaitClose()
    }

    private fun calcTemperature(temperature: Float): Float = temperature - MIDDLE_RATING

    companion object {
        private const val MIDDLE_RATING = 3.0f
    }
}