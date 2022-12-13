package com.gta.data

import com.gta.data.model.Car
import com.gta.data.repository.ReservationRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class ReservationRepositoryUnitTest(
    @Mock private val reservationDataSource: ReservationDataSource,
    @Mock private val carDataSource: CarDataSource
) {
    private val repository = ReservationRepositoryImpl(reservationDataSource, carDataSource)

    private val GOOD_ID = "good"
    private val BAD_ID = "bad"

    private val GOOD_RES = Reservation(carId = GOOD_ID)
    private val BAD_RES = Reservation(carId = BAD_ID)

    private val STATE = ReservationState.DONE

    @BeforeEach
    fun init() {
        runBlocking {
            `when`(carDataSource.getCar(GOOD_ID)).thenReturn(flow { emit(Car()) })
            `when`(carDataSource.getCar(BAD_ID)).thenReturn(flow { emit(null) })
            `when`(reservationDataSource.createReservation(eq(GOOD_RES), anyString())).thenReturn(
                flow { emit(true) }
            )
            `when`(reservationDataSource.createReservation(eq(BAD_RES), anyString())).thenReturn(
                flow { emit(false) }
            )
            `when`(reservationDataSource.getReservation(GOOD_ID)).thenReturn(flow { emit(Reservation()) })
            `when`(reservationDataSource.getReservation(BAD_ID)).thenReturn(flow { emit(null) })

            `when`(reservationDataSource.updateReservationState(GOOD_ID, STATE.state)).thenReturn(
                flow { emit(true) }
            )
            `when`(reservationDataSource.updateReservationState(BAD_ID, STATE.state)).thenReturn(
                flow { emit(false) }
            )
        }
    }

    @Test
    @DisplayName("createReservation : 예약을 받으면 UCMCResult(String)를 리턴한다.")
    fun Should_Success_When_createReservation() {
        runBlocking {
            val result = repository.createReservation(GOOD_RES)
            Assertions.assertTrue(result is UCMCResult.Success && result.data != "")
        }
    }

    @Test
    @DisplayName("createReservation : 예약을 받으면 UCMCError을 리턴한다.")
    fun Should_Success_When_createReservationFail() {
        runBlocking {
            val result = repository.createReservation(BAD_RES)
            Assertions.assertTrue(result is UCMCResult.Error)
        }
    }

    @Test
    @DisplayName("getReservationInfo : ID을 받으면 Reservation를 리턴한다.")
    fun Should_Success_When_getReservationInfo() {
        runBlocking {
            val result = repository.getReservationInfo(GOOD_ID).first()
            Assertions.assertTrue(result is UCMCResult.Success)
        }
    }

    @Test
    @DisplayName("getReservationInfo : ID을 받으면 FirestorException를 리턴한다.")
    fun Should_FirestoreException_When_getReservationInfo() {
        runBlocking {
            val result = repository.getReservationInfo(BAD_ID).first()
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getReservationCar : ID를 받으면 carID를 리턴한다.")
    fun Should_Success_When_getReservationCar() {
        runBlocking {
            val result = repository.getReservationCar(GOOD_ID)
            Assertions.assertNotEquals("", result.first())
        }
    }

    @Test
    @DisplayName("getReservationCar : ID를 받으면 빈 스트링을 리턴한다.")
    fun Should_Success_When_getReservationCar_Fail() {
        runBlocking {
            val result = repository.getReservationCar(BAD_ID)
            Assertions.assertEquals("", result.first())
        }
    }

    @Test
    @DisplayName("updateReservationState : 정상적인 아이디와 상태가 들어오면 Success를 리턴한다.")
    fun Should_Success_When_updateReservationState() {
        runBlocking {
            val result = repository.updateReservationState(GOOD_ID, STATE)
            Assertions.assertTrue(result is UCMCResult.Success)
        }
    }

    @Test
    @DisplayName("updateReservationState : 비정상적인 입력, 또는 비정상적인 동작 시 Error를 리턴한다.")
    fun Should_Success_When_updateReservationState_Fail() {
        runBlocking {
            val result = repository.updateReservationState(BAD_ID, STATE)
            Assertions.assertFalse(result is UCMCResult.Error)
        }
    }
}
