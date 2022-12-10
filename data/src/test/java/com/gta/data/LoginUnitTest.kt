package com.gta.data

import android.content.res.Resources.NotFoundException
import com.gta.data.model.UserInfo
import com.gta.data.repository.LoginRepositoryImpl
import com.gta.data.source.LoginDataSource
import com.gta.data.source.MessageTokenDataSource
import com.gta.data.source.UserDataSource
import com.gta.data.util.BAD_UID
import com.gta.data.util.EXCEPTION_UID
import com.gta.data.util.GOOD_UID
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LoginRepository
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
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class LoginUnitTest (
    @Mock private val userDataSource: UserDataSource,
    @Mock private val loginDataSource: LoginDataSource,
    @Mock private val messageTokenDataSource: MessageTokenDataSource
) {

    private val repository: LoginRepository =
        LoginRepositoryImpl(userDataSource, loginDataSource, messageTokenDataSource)

    @BeforeEach
    fun init() {
        `when`(userDataSource.getUser(anyString())).thenReturn(flow { emit(null) })
        `when`(userDataSource.getUser(GOOD_UID)).thenReturn(flow { emit(UserInfo()) })
        `when`(messageTokenDataSource.getMessageToken()).thenReturn(flow { emit("token") })
    }

    @Test
    @DisplayName("checkCurrentUser : 이미 가입된 유저는 Success(Unit)을 반환한다.")
    fun Should_Success_When_checkUserWithGooduid() {
        runBlocking {
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.checkCurrentUser(GOOD_UID))
        }
    }

    @Test
    @DisplayName("checkCurrentUser : 새로 가입하는 유저는 Error(NotFoundException)을 반환한다.")
    fun Should_NotFoundException_When_checkUserWithBaduid() {
        runBlocking {
            val result = repository.checkCurrentUser(BAD_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is NotFoundException)
        }
    }

    @Test
    @DisplayName("signUp : 이미 가입된 유저는 유저 생성 메소드를 수행하지 않고 Success(Unit)을 반환한다.")
    fun Should_Success_When_signUpWithGooduid() {
        runBlocking {
            val result = repository.signUp(GOOD_UID)
            Assertions.assertEquals(UCMCResult.Success(Unit), result)
            verify(loginDataSource, never()).createUser(eq(GOOD_UID), any())
        }
    }

    @Test
    @DisplayName("signUp : 새로 가입하는 유저는 유저 생성 메소드를 수행하고 Success(Unit)을 반환한다.")
    fun Should_Success_When_signUpWithBaduid() {
        `when`(loginDataSource.createUser(eq(BAD_UID), anyString())).thenReturn(flow { emit(true) })
        runBlocking {
            val result = repository.signUp(BAD_UID)
            Assertions.assertEquals(UCMCResult.Success(Unit), result)
            verify(loginDataSource, times(1)).createUser(eq(BAD_UID), any())
        }
    }

    @Test
    @DisplayName("signUp : 유저 생성 메소드가 실패하면 Error(FirestoreException)을 반환한다.")
    fun Should_FirestoreException_When_createUserFailed() {
        `when`(loginDataSource.createUser(eq(EXCEPTION_UID), anyString())).thenReturn(flow { emit(false) })
        runBlocking {
            val result = repository.signUp(EXCEPTION_UID)
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }

    @Test
    @DisplayName("updateUserMessageToken : 유효한 uid를 받으면 토큰을 업데이트하고 true를 반환한다.")
    fun Should_True_When_updateUserMessageTokenWithGooduid() {
        `when`(userDataSource.updateUserMessageToken(eq(GOOD_UID), anyString())).thenReturn( flow { emit(true) } )
        runBlocking {
            Assertions.assertTrue(repository.updateUserMessageToken(GOOD_UID))
        }
    }

    @Test
    @DisplayName("updateUserMessageToken : 유효하지 않은 uid를 받으면 false를 반환한다.")
    fun Should_False_When_updateUserMessageTokenWithBaduid() {
        `when`(userDataSource.updateUserMessageToken(eq(BAD_UID), anyString())).thenReturn( flow { emit(false) } )
        runBlocking {
            Assertions.assertFalse(repository.updateUserMessageToken(BAD_UID))
        }
    }
}
