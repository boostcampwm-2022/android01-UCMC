package com.gta.data

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.model.toSimple
import com.gta.data.model.update
import com.gta.data.repository.CarRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.StorageDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Coordinate
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UpdateCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CarRepositoryUnitTest(
    @Mock private val userDataSource: UserDataSource,
    @Mock private val carDataSource: CarDataSource,
    @Mock private val reservationDataSource: ReservationDataSource,
    @Mock private val storageDataSource: StorageDataSource
) {
    private val repository: CarRepository =
        CarRepositoryImpl(userDataSource, carDataSource, reservationDataSource, storageDataSource)

    val AVAILABLE_CAR = "AvailableCar"
    val UNAVAILABLE_CAR = "UnavailableCar"
    val RENTED_CAR = "RentedCar"

    private val GOOD_UPDATECAR = UpdateCar(
        price = 1000,
        rentState = RentState.AVAILABLE,
        availableDate = AvailableDate(),
        location = "",
        coordinate = Coordinate()
    )

    private val GOOD_CAR_LIST = listOf(GOOD_UID)
    private val BAD_CAR_LIST = listOf(BAD_UID)

    val EMPTY_CAR_USER = "EmptyCarUser"
    val BAD_CAR_USER = "BadCarUser"

    @BeforeEach
    fun init() {
        Mockito.`when`(userDataSource.getUser(GOOD_UID)).thenReturn(flow { emit(UserInfo()) })
        Mockito.`when`(userDataSource.getUser(BAD_UID)).thenReturn(flow { emit(null) })

        Mockito.`when`(carDataSource.getCar(GOOD_UID))
            .thenReturn(flow { emit(Car(ownerId = GOOD_UID)) })
        Mockito.`when`(carDataSource.getCar(BAD_UID)).thenReturn(flow { emit(null) })

        Mockito.`when`(carDataSource.getCar(UNAVAILABLE_CAR))
            .thenReturn(flow { emit(Car(rentState = "대여 불가능")) })
        Mockito.`when`(carDataSource.getCar(AVAILABLE_CAR))
            .thenReturn(flow { emit(Car(rentState = "대여 가능")) })
        Mockito.`when`(carDataSource.getCar(RENTED_CAR))
            .thenReturn(flow { emit(Car(rentState = "대여중")) })

        Mockito.`when`(
            carDataSource.createCar(
                GOOD_UID,
                Car(ownerId = GOOD_UID).update(GOOD_UPDATECAR)
            )
        )
            .thenReturn(flow { emit(true) })
        Mockito.`when`(carDataSource.createCar(BAD_UID, Car().update(GOOD_UPDATECAR)))
            .thenReturn(flow { emit(false) })

        Mockito.`when`(userDataSource.getUser(GOOD_UID))
            .thenReturn(flow { emit(UserInfo(myCars = GOOD_CAR_LIST)) })
        Mockito.`when`(userDataSource.getUser(BAD_UID))
            .thenReturn(flow { emit(null) })
        Mockito.`when`(userDataSource.getUser(EMPTY_CAR_USER))
            .thenReturn(flow { emit(UserInfo(myCars = emptyList())) })
        Mockito.`when`(userDataSource.getUser(BAD_CAR_USER))
            .thenReturn(flow { emit(UserInfo(myCars = BAD_CAR_LIST)) })

        Mockito.`when`(carDataSource.getOwnerCars(GOOD_CAR_LIST))
            .thenReturn(flow { emit(listOf(Car())) })
        Mockito.`when`(carDataSource.getOwnerCars(BAD_CAR_LIST))
            .thenReturn(flow { emit(null) })
    }

    @Test
    @DisplayName("getOwnerId : 정상적인 차 uid의 경우 Success(GOOD_UID)를 리턴한다.")
    fun Should_Success_When_getOwnerIdWithGooduid() {
        runBlocking {
            val result = repository.getOwnerId(GOOD_UID).first()
            Assertions.assertEquals(result, UCMCResult.Success(GOOD_UID))
        }
    }

    @Test
    @DisplayName("getOwnerId : 비정상적인 차 uid의 경우 Error(FirestoreException())를 리턴한다.")
    fun Should_FirestoreException_When_getOwnerIdWithBaduid() {
        runBlocking {
            val result = repository.getOwnerId(BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getCarRentState : 대여 불가능인 상태의 차일 경우 Success(RentState.UNAVAILABLE)를 리턴한다.")
    fun Should_Success_When_getCarRentStateWithUnavilableCar() {
        runBlocking {
            val result = repository.getCarRentState(UNAVAILABLE_CAR).first()
            Assertions.assertEquals(result, UCMCResult.Success(RentState.UNAVAILABLE))
        }
    }

    @Test
    @DisplayName("getCarRentState : 대여 가능인 상태의 차일 경우 Success(RentState.AVAILABLE)를 리턴한다.")
    fun Should_Success_When_getCarRentStateWithAvilableCar() {
        runBlocking {
            val result = repository.getCarRentState(AVAILABLE_CAR).first()
            Assertions.assertEquals(result, UCMCResult.Success(RentState.AVAILABLE))
        }
    }

    @Test
    @DisplayName("getCarRentState : 대여중인 상태의 차일 경우 Success(RentState.RENTED)를 리턴한다.")
    fun Should_Success_When_getCarRentStateWithRentedCar() {
        runBlocking {
            val result = repository.getCarRentState(RENTED_CAR).first()
            Assertions.assertEquals(result, UCMCResult.Success(RentState.RENTED))
        }
    }

    @Test
    @DisplayName("getCarRentState : 비정상적인 차 uid일 경우 Error(FirestoreException())를 리턴한다.")
    fun Should_FirestoreException_When_getCarRentStateWithBaduid() {
        runBlocking {
            val result = repository.getCarRentState(BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("updateCarDetail : 정상적으로 차 데이터를 업데이트할 경우 true를 리턴한다.")
    fun Should_True_When_updateCarDetailWithGooduid() {
        runBlocking {
            val result = repository.updateCarDetail(GOOD_UID, GOOD_UPDATECAR).first()
            Assertions.assertTrue(result)
        }
    }

    @Test
    @DisplayName("updateCarDetail : 비정상적인 차 uid로 차 데이터를 업데이트할 경우 false를 리턴한다.")
    fun Should_False_When_updateCarDetailWithBaduid() {
        runBlocking {
            val result = repository.updateCarDetail(BAD_UID, GOOD_UPDATECAR).first()
            Assertions.assertFalse(result)
        }
    }

    @Test
    @DisplayName("getSimpleCarList : 정상적인 차주 uid일 경우 Success(listOf(SimpleCar())를 리턴한다.")
    fun Should_Success_When_getSimpleCarListWithGooduid() {
        runBlocking {
            val result = repository.getSimpleCarList(GOOD_UID).first()
            Assertions.assertEquals(
                result,
                UCMCResult.Success(listOf(Car().toSimple(Car().pinkSlip.informationNumber)))
            )
        }
    }

    @Test
    @DisplayName("getSimpleCarList : 비정상적인 차주 uid일 경우 Error(FirestoreException())를 리턴한다.")
    fun Should_FirestoreException_When_getSimpleCarListWithBaduid() {
        runBlocking {
            val result = repository.getSimpleCarList(BAD_UID).first()
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("getSimpleCarList : 내차 목록이 없는 차주일 경우 UCMCResult.Success(emptyList())를 리턴한다.")
    fun Should_Success_When_getSimpleCarListWithEmptyCarUser() {
        runBlocking {
            val result = repository.getSimpleCarList(EMPTY_CAR_USER).first()
            Assertions.assertEquals(result, UCMCResult.Success<List<SimpleCar>>(emptyList()))
        }
    }

    @Test
    @DisplayName("getSimpleCarList : 비정상적인 내차 목록이 없는 차주일 경우 UCMCResult.Error(FirestoreException())를 리턴한다.")
    fun Should_FirestoreException_When_getSimpleCarListWithBadCarUser() {
        runBlocking {
            val result = repository.getSimpleCarList(BAD_CAR_USER).first()
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }
}
