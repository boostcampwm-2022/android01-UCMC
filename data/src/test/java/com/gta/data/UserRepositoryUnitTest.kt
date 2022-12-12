package com.gta.data

import com.gta.data.model.UserInfo
import com.gta.data.repository.UserRepositoryImpl
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserRepositoryUnitTest(
    @Mock private val userDataSource: UserDataSource,
    @Mock private val reservationDataSource: ReservationDataSource
) {
    private val repository: UserRepository =
        UserRepositoryImpl(userDataSource, reservationDataSource)

    @BeforeEach
    fun init() {
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(flow { emit(UserInfo()) })
        `when`(userDataSource.getUser(BAD_UID)).thenReturn(flow { emit(null) })

        `when`(reservationDataSource.getRentingStateReservations(GOOD_UID)).thenReturn(
            flow { emit(listOf(SimpleReservation(carId = GOOD_UID))) }
        )
        `when`(reservationDataSource.getRentingStateReservations(BAD_UID)).thenReturn(
            flow { emit(emptyList()) }
        )
    }

    @Test
    @DisplayName("getUserProfile : 정상적인 사용자 uid의 경우 Success(UserProfile)를 리턴한다.")
    fun Should_Success_When_getUserProfileWithGooduid() {
        runBlocking {
            val result = repository.getUserProfile(GOOD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Success<UserProfile>)
        }
    }

    @Test
    @DisplayName("getUserProfile : 존재하지 않는 사용자 uid인 Erro(FirestoreException())를 리턴한다.")
    fun Should_FirestoreException_When_getUserProfileWithBaduid() {
        runBlocking {
            val result = repository.getUserProfile(BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Error)

            if (result is UCMCResult.Error) {
                Assertions.assertTrue(result.e is FirestoreException)
            }
        }
    }

    @Test
    @DisplayName("getNowReservation : 현재 차의 현재 대여중인 예약이 있다면 디폴트 값이 아닌 Success(SimpleReservation)를 리턴한다.")
    fun Should_Success_When_getNowReservationWithGooduidExistenceNowRent() {
        runBlocking {
            val result = repository.getNowReservation(GOOD_UID, GOOD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Success<SimpleReservation>)

            if (result is UCMCResult.Success<SimpleReservation>) {
                Assertions.assertNotEquals(result.data, SimpleReservation())
            }
        }
    }

    @Test
    @DisplayName("getNowReservation : 현재 차의 현재 대여중인 예약이 없다면 디폴트 값인 Success(SimpleReservation)를 리턴한다.")
    fun Should_Success_When_getNowReservationWithGooduidNotExistenceNowRent() {
        runBlocking {
            val result = repository.getNowReservation(GOOD_UID, BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Success<SimpleReservation>)

            if (result is UCMCResult.Success<SimpleReservation>) {
                Assertions.assertEquals(result.data, SimpleReservation())
            }
        }
    }

    @Test
    @DisplayName("getNowReservation : 현재 차의 현재 대여중인 예약이 없다면 디폴트 값인 Success(SimpleReservation)를 리턴한다.")
    fun Should_DefaultSimpleReservation_When_getNowReservationWithBaduid() {
        runBlocking {
            val result = repository.getNowReservation(BAD_UID, BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Success<SimpleReservation>)

            if (result is UCMCResult.Success<SimpleReservation>) {
                Assertions.assertEquals(result.data, SimpleReservation())
            }
        }
    }
}
