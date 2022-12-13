package com.gta.data

import com.gta.data.model.UserInfo
import com.gta.data.repository.NotificationRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.NotificationDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Notification
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class NotificationRepositoryUnitTest(
    @Mock private val notificationDataSource: NotificationDataSource,
    @Mock reservationDataSource: ReservationDataSource,
    @Mock private val userDataSource: UserDataSource,
    @Mock carDataSource: CarDataSource
) {
    private val repository: NotificationRepository = NotificationRepositoryImpl(
        notificationDataSource,
        reservationDataSource,
        userDataSource,
        carDataSource
    )
    private val notification: Notification = Notification()

    @BeforeEach
    fun init() {
        `when`(notificationDataSource.saveNotification(any(), anyString(), anyString())).thenReturn(flow { emit(false) })
        `when`(notificationDataSource.saveNotification(any(), eq(GOOD_UID), anyString())).thenReturn(flow { emit(true) })
    }

    @Test
    @DisplayName("saveNotification: 유효한 uid일 떄 Notification 저장에 성공하면 Success(Unit)을 리턴 한다.")
    fun Should_Success_When_saveNotificationWithGoodUid() = runBlocking {
        val result = repository.saveNotification(notification, GOOD_UID)

        Assertions.assertTrue(result is UCMCResult.Success<Unit>)
    }

    @Test
    @DisplayName("saveNotification: Notificaiton 저장에 실패하면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_saveNotificationWithBadUid() = runBlocking {
        val result = repository.saveNotification(notification, BAD_UID)

        Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
    }

    @Test
    @DisplayName("sendNotification: 유효한 uid일 떄 Notification 전송에 성공하면 Success(Unit)을 리턴한다.")
    fun Should_Success_When_sendNotificationWithGoodUid() = runBlocking {
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(flow { emit(UserInfo(messageToken = GOOD_TOKEN)) })
        `when`(notificationDataSource.sendNotification(notification, GOOD_TOKEN)).thenReturn(true)

        val result = repository.sendNotification(notification, GOOD_UID)

        Assertions.assertTrue(result is UCMCResult.Success<Unit>)
    }

    @Test
    @DisplayName("sendNotification: 유효하지 않은 uid일 때 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_sendNotificationWithBadUid() {
        `when`(userDataSource.getUser(anyString())).thenReturn(flow { emit(null) })

        runBlocking {
            val result = repository.sendNotification(notification, BAD_UID)

            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
            verify(notificationDataSource, never()).sendNotification(notification, GOOD_TOKEN)
        }
    }

    @Test
    @DisplayName("sendNotification: 유효하지 않은 토큰일 때 Notification 전송에 실패하면 Error(FirestoreException)를 리턴한다.")
    fun Should_FirestoreException_When_sendNotificationWithBadToken() = runBlocking {
        `when`(userDataSource.getUser(eq(GOOD_UID))).thenReturn(flow { emit(UserInfo(messageToken = BAD_TOKEN)) })
        `when`(notificationDataSource.sendNotification(notification, BAD_TOKEN)).thenReturn(false)

        val result = repository.sendNotification(notification, GOOD_UID)

        Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
    }

    companion object {
        private const val BAD_TOKEN = "bad"
        private const val GOOD_TOKEN = "good"
    }
}
