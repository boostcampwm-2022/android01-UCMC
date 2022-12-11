package com.gta.data

import com.gta.data.model.UserInfo
import com.gta.data.repository.ReportRepositoryImpl
import com.gta.data.source.UserDataSource
import com.gta.domain.model.CoolDownException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.ReportRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class ReportRepositoryUnitTest(
    @Mock private val userDataSource: UserDataSource
) {

    private val repository: ReportRepository = ReportRepositoryImpl(userDataSource)

    @BeforeEach
    fun init() {
        `when`(userDataSource.addReportCount(anyString(), anyInt())).thenReturn(flow { emit(false) })
        `when`(userDataSource.addReportCount(eq(GOOD_UID), anyInt())).thenReturn(flow { emit(true) })
        `when`(userDataSource.getUser(anyString())).thenReturn(flow { emit(null) })
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(flow { emit(UserInfo()) })
    }

    @Test
    @DisplayName("reportUser : 유효한 uid를 매개변수로 받으면 Success(Unit)을 리턴한다.")
    fun Should_Success_When_Gooduid() {
        runBlocking {
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.reportUser(GOOD_UID))
        }
    }

    @Test
    @DisplayName("reportUser : 유효하지 않은 uid를 매개변수로 받으면 Error(FirestoreException)을 리턴한다.")
    fun Should_FirestoreException_When_Baduid() {
        runBlocking {
            val result = repository.reportUser(BAD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("reportUser : 메소드를 호출해서 Success(Unit)을 리턴받고 10초안에 다시 호출하면 Error(CoolDownException)을 리턴한다.")
    fun Should_Success_When_SuccessAndCallIn10seconds() {
        runBlocking {
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.reportUser(GOOD_UID))
            val result = repository.reportUser(GOOD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is CoolDownException)
        }
    }

    @Test
    @DisplayName("reportUser : 메소드를 호출해서 Error를 리턴받으면 10초의 대기시간이 적용되지 않는다.")
    fun Should_Success_When_ErrorAndCallIn10seconds() {
        runBlocking {
            val result = repository.reportUser(BAD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.reportUser(GOOD_UID))
        }
    }

    @Test
    @DisplayName("reportUser : 메소드를 호출한 뒤 10초뒤에 다시 호출하면 Success(Unit)을 리턴한다.")
    fun Should_Success_When_SuccessAndCallAfter10seconds() {
        runBlocking {
            val currentTime = System.currentTimeMillis()
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.reportUser(GOOD_UID, currentTime))
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.reportUser(GOOD_UID, currentTime + 10000))
        }
    }
}
