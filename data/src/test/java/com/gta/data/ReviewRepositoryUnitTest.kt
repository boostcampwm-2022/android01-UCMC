package com.gta.data

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.repository.ReviewRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.ReviewDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DuplicatedItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReviewDTO
import com.gta.domain.model.ReviewType
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserReview
import com.gta.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyFloat
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class ReviewRepositoryUnitTest(
    @Mock private val reviewDataSource: ReviewDataSource,
    @Mock private val carDataSource: CarDataSource,
    @Mock private val reservationDataSource: ReservationDataSource,
    @Mock private val userDataSource: UserDataSource
) {

    private val repository: ReviewRepository =
        ReviewRepositoryImpl(reviewDataSource, carDataSource, reservationDataSource, userDataSource)

    @BeforeEach
    fun init() {
        `when`(userDataSource.getUser(anyString())).thenReturn(
            flow { emit(UserInfo()) }
        )
        `when`(userDataSource.getUser(BAD_UID)).thenReturn(
            flow { emit(null) }
        )
        `when`(reviewDataSource.addReview(eq(GOOD_UID), eq(GOOD_RESERVATION), any())).thenReturn(
            flow { emit(true) }
        )
        `when`(reviewDataSource.updateTemperature(eq(GOOD_UID), anyFloat())).thenReturn(
            flow { emit(true) }
        )
        `when`(reservationDataSource.getReservation(anyString())).thenReturn(
            flow { emit(Reservation()) }
        )
        `when`(reservationDataSource.getReservation(BAD_RESERVATION)).thenReturn(
            flow { emit(null) }
        )
        `when`(carDataSource.getCar(anyString())).thenReturn(
            flow { emit(Car()) }
        )
        `when`(carDataSource.getCar(BAD_CAR)).thenReturn(
            flow { emit(null) }
        )
        `when`(reviewDataSource.isExistReview(anyString(), eq(GOOD_RESERVATION))).thenReturn(
            flow { emit(false) }
        )
        `when`(reviewDataSource.isExistReview(anyString(), eq(DUPLICATED_RESERVATION))).thenReturn(
            flow { emit(true) }
        )
    }

    @Test
    @DisplayName("addReview : 유효한 uid, reservationId, UserReview를 받으면 Success(Unit)을 리턴한다.")
    fun Should_Success_When_addReviewWithGoodUidAndReservationId() {
        runBlocking {
            Assertions.assertEquals(
                UCMCResult.Success(Unit),
                repository.addReview(GOOD_UID, GOOD_RESERVATION, UserReview())
            )
        }
    }

    @Test
    @DisplayName("addReview : 유효하지 않은 uid를 받으면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_addReviewWithBaduid() {
        runBlocking {
            val result = repository.addReview(BAD_UID, GOOD_RESERVATION, UserReview())
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("addReview : addReview 쿼리가 실패하면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_addReviewFailed() {
        `when`(reviewDataSource.addReview(eq(GOOD_UID), eq(GOOD_RESERVATION), any())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.addReview(GOOD_UID, GOOD_RESERVATION, UserReview())
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("addReview : updateTemperature 쿼리가 실패하면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_updateTemperatureFailed() {
        `when`(reviewDataSource.updateTemperature(eq(GOOD_UID), anyFloat())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.addReview(GOOD_UID, GOOD_RESERVATION, UserReview())
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getReviewDTO : 유효한 uid, reservationId를 받으면 Success(Unit)을 리턴한다.")
    fun Should_FirestoreException_When_getReviewDTOWithGoodUidAndReservation() {
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(result is UCMCResult.Success<ReviewDTO>)
        }
    }

    @Test
    @DisplayName("getReviewDTO : 유효하지 않은 reservationId를 받으면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_getReviewDTOWithBadReservationId() {
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, BAD_RESERVATION)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getReviewDTO : 이미 리뷰한 reservationId를 받으면 Error(DuplicatedItemException)를 리턴한다.")
    fun Should_FirestoreException_When_getReviewDTOWithDuplicatedReservationId() {
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, DUPLICATED_RESERVATION)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is DuplicatedItemException)
        }
    }

    @Test
    @DisplayName("getReviewDTO : reservation에 들어있는 carId가 유효하지 않다면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_getReviewDTOWithBadCarId() {
        `when`(reservationDataSource.getReservation(GOOD_RESERVATION)).thenReturn(
            flow { emit(Reservation(carId = BAD_CAR)) }
        )
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getReviewDTO : 상대방의 uid가 유효하지 않다면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_getReviewDTOWithBaduid() {
        `when`(reservationDataSource.getReservation(GOOD_RESERVATION)).thenReturn(
            flow { emit(Reservation(lenderId = BAD_UID, ownerId = GOOD_UID)) }
        )
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getReviewDTO : 리턴 받은 ReviewDTO의 ReviewType을 통해 리뷰의 유형을 확인할 수 있다.")
    fun Should_KnowReviewType_When_getReviewDTOSuccess() {
        runBlocking {
            `when`(reservationDataSource.getReservation(GOOD_RESERVATION)).thenReturn(
                flow { emit(Reservation(lenderId = GOOD_UID, ownerId = "other")) }
            )
            val lenderToOwner = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(
                lenderToOwner is UCMCResult.Success && lenderToOwner.data.reviewType == ReviewType.LENDER_TO_OWNER
            )
            `when`(reservationDataSource.getReservation(GOOD_RESERVATION)).thenReturn(
                flow { emit(Reservation(lenderId = "other", ownerId = GOOD_UID)) }
            )
            val ownerToLender = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(
                ownerToLender is UCMCResult.Success && ownerToLender.data.reviewType == ReviewType.OWNER_TO_LENDER
            )
        }
    }

    @Test
    @DisplayName("getReviewDTO : 자동차의 이미지 리스트가 비어 있으면 리턴받은 ReviewType의 carImage엔 빈 값이 들어간다.")
    fun Should_EmptyUri_When_ImagesIsEmpty() {
        `when`(carDataSource.getCar(anyString())).thenReturn(
            flow { emit(Car(images = emptyList())) }
        )
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(
                result is UCMCResult.Success && result.data.carImage.isEmpty()
            )
        }
    }

    @Test
    @DisplayName("getReviewDTO : 자동차의 이미지 리스트가 비어 있지 않다면 리턴받은 ReviewType의 carImage엔 리스트의 첫 번째 요소가 들어간다.")
    fun Should_ImagesFirstElement_When_ImagesIsNotEmpty() {
        val images = listOf("image1", "image2", "image3")
        `when`(carDataSource.getCar(anyString())).thenReturn(
            flow { emit(Car(images = images)) }
        )
        runBlocking {
            val result = repository.getReviewDTO(GOOD_UID, GOOD_RESERVATION)
            Assertions.assertTrue(
                result is UCMCResult.Success && result.data.carImage == images.first()
            )
        }
    }
}
