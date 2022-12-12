package com.gta.data

import com.gta.data.repository.MessageTokenRepositoryImpl
import com.gta.data.source.MessageTokenDataSource
import com.gta.domain.repository.MessageTokenRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MessageTokenRepositoryUnitTest(@Mock private val messageTokenDataSource: MessageTokenDataSource) {
    private val repository: MessageTokenRepository =
        MessageTokenRepositoryImpl(messageTokenDataSource)
    private val token = anyString()

    @Test
    @DisplayName("setMessageToken: 토큰이 정상적으로 저장되면 경우 true를 반환한다.")
    fun Should_True_When_storeTokenSuccess() = runBlocking {
        `when`(messageTokenDataSource.setMessageToken(token)).thenReturn(true)

        val result = repository.setMessageToken(token)

        Assertions.assertEquals(true, result)
    }

    @Test
    @DisplayName("setMessageToken: 토큰이 정상적으로 저장되지 않으면 false를 반환한다.")
    fun Should_False_When_storeTokenFail() = runBlocking {
        `when`(messageTokenDataSource.setMessageToken(token)).thenReturn(false)

        val result = repository.setMessageToken(token)

        Assertions.assertEquals(false, result)
    }
}