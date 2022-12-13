package com.gta.data

import com.gta.data.repository.NicknameRepositoryImpl
import com.gta.data.source.NicknameDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.NicknameState
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.NicknameRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq

@ExtendWith(MockitoExtension::class)
class NicknameRepositoryUnitTest(
    @Mock private val nicknameDataSource: NicknameDataSource
) {

    private val repository: NicknameRepository = NicknameRepositoryImpl(nicknameDataSource)

    @Test
    @DisplayName("checkNicknameState : size가 2이상, 한글,영문,숫자만 포함된 nickname을 받으면 NicknameState.GREAT를 리턴한다.")
    fun Should_GREAT_When_checkNicknameStateWithGoodCondition() {
        Assertions.assertEquals(NicknameState.GREAT, repository.checkNicknameState("굿동훈"))
        Assertions.assertEquals(NicknameState.GREAT, repository.checkNicknameState("배드동훈"))
        Assertions.assertEquals(NicknameState.GREAT, repository.checkNicknameState("Good동훈"))
        Assertions.assertEquals(NicknameState.GREAT, repository.checkNicknameState("Bad동훈123"))
    }

    @Test
    @DisplayName("checkNicknameState : size가 2미만인 nickname을 받으면 NicknameState.SHORT_LENGTH를 리턴한다.")
    fun Should_SHORT_LENGTH_When_checkNicknameStateWithNicknameLessThan2() {
        Assertions.assertEquals(NicknameState.SHORT_LENGTH, repository.checkNicknameState(""))
        Assertions.assertEquals(NicknameState.SHORT_LENGTH, repository.checkNicknameState("굿"))
    }

    @Test
    @DisplayName("checkNicknameState : 한글,영문,숫자 이외의 문자가 포함된 nickname을 받으면 NicknameState.CONTAIN_SYMBOL를 리턴한다.")
    fun Should_CONTAIN_SYMBOL_When_checkNicknameStateWithNicknameContainSymbol() {
        Assertions.assertEquals(NicknameState.CONTAIN_SYMBOL, repository.checkNicknameState("굿동훈 "))
        Assertions.assertEquals(NicknameState.CONTAIN_SYMBOL, repository.checkNicknameState("★굿동훈★"))
        Assertions.assertEquals(NicknameState.CONTAIN_SYMBOL, repository.checkNicknameState("굿-동-훈"))
    }

    @Test
    @DisplayName("updateNickname : 유효한 uid를 받으면 nickname을 업데이트하고 Success(Unit)를 반환한다.")
    fun Should_SUCCESS_When_updateNicknameWithGooduid() {
        `when`(nicknameDataSource.updateNickname(eq(GOOD_UID), anyString())).thenReturn(
            flow { emit(true) }
        )
        runBlocking {
            Assertions.assertEquals(UCMCResult.Success(Unit), repository.updateNickname(GOOD_UID, "굿동훈"))
        }
    }

    @Test
    @DisplayName("updateNickname : 유효하지 않은 uid를 받으면 nickname을 업데이트하고 Error(FirestoreException)를 반환한다.")
    fun Should_FirestoreException_When_updateNicknameWithBaduid() {
        `when`(nicknameDataSource.updateNickname(eq(BAD_UID), anyString())).thenReturn(
            flow { emit(false) }
        )
        runBlocking {
            val result = repository.updateNickname(BAD_UID, "굿동훈")
            Assertions.assertTrue(result is UCMCResult.Error && result.e is FirestoreException)
        }
    }
}
