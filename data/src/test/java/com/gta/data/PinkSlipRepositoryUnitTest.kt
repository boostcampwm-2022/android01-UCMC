package com.gta.data

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.repository.PinkSlipRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.PinkSlipDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DuplicatedItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.PinkSlip
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class PinkSlipRepositoryUnitTest(
    @Mock private val carDataSource: CarDataSource,
    @Mock private val userDataSource: UserDataSource,
    @Mock private val pinkSlipDataSource: PinkSlipDataSource
) {

    private val repository: PinkSlipRepository =
        PinkSlipRepositoryImpl(carDataSource, userDataSource, pinkSlipDataSource)

    @BeforeEach
    fun init() {
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(
            flow { emit(UserInfo()) }
        )
        `when`(userDataSource.getUser(BAD_UID)).thenReturn(
            flow { emit(null) }
        )
        `when`(pinkSlipDataSource.updateCars(eq(GOOD_UID), anyList())).thenReturn(
            flow { emit(true) }
        )
        `when`(carDataSource.createCar(anyString(), any())).thenReturn(
            flow { emit(true) }
        )
        `when`(carDataSource.getCar(NEW_CAR)).thenReturn(
            flow { emit(null) }
        )
        `when`(carDataSource.getCar(DUPLICATED_CAR)).thenReturn(
            flow { emit(Car()) }
        )
    }

    @Test
    @DisplayName("setPinkSlip : 유효한 uid와 pinkSlip을 받으면 Success(Unit)을 리턴한다.")
    fun Should_Success_When_GooduidAndPinkslip() {
        runBlocking {
            Assertions.assertEquals(
                UCMCResult.Success(Unit),
                repository.setPinkSlip(GOOD_UID, PinkSlip(informationNumber = NEW_CAR))
            )
        }
    }

    @Test
    @DisplayName("setPinkSlip : 유효하지 않은 uid를 받으면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_Baduid() {
        runBlocking {
            val result = repository.setPinkSlip(BAD_UID, PinkSlip(informationNumber = NEW_CAR))
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("setPinkSlip : pinkSlip에 informationNumber가 이미 등록된 번호라면 Error(DuplicatedItemException)을 리턴한다.")
    fun Should_FirestoreException_When_DuplicatedInformationId() {
        runBlocking {
            val result = repository.setPinkSlip(GOOD_UID, PinkSlip(informationNumber = DUPLICATED_CAR))
            Assertions.assertTrue(result is UCMCResult.Error && result.e is DuplicatedItemException)
        }
    }

    @Test
    @DisplayName("setPinkSlip : updateCars 쿼리가 실패하면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_updateCarsFailed() {
        `when`(pinkSlipDataSource.updateCars(anyString(), anyList())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.setPinkSlip(GOOD_UID, PinkSlip(informationNumber = NEW_CAR))
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("setPinkSlip : createCar 쿼리가 실패하면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_createCarsFailed() {
        `when`(carDataSource.createCar(anyString(), any())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.setPinkSlip(GOOD_UID, PinkSlip(informationNumber = NEW_CAR))
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    companion object {
        private const val NEW_CAR = "newCar"
        private const val DUPLICATED_CAR = "duplicatedCar"
    }
}
