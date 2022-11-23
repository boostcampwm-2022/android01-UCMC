package com.gta.data.repository

import com.gta.data.source.NicknameDataSource
import com.gta.domain.model.NicknameState
import com.gta.domain.repository.NicknameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NicknameRepositoryImpl @Inject constructor(
    private val dataSource: NicknameDataSource
) : NicknameRepository {
    override fun checkNicknameState(nickname: String): NicknameState =
        when {
            nickname.length < MIN_LENGTH -> NicknameState.SHORT_LENGTH
            SYMBOL_REGEX.containsMatchIn(nickname) -> NicknameState.CONTAIN_SYMBOL
            else -> NicknameState.GREAT
        }

    override fun updateNickname(uid: String, nickname: String): Flow<Boolean> = callbackFlow {
        dataSource.updateNickname(uid, nickname).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }

    companion object {
        private const val MIN_LENGTH = 2
        private val SYMBOL_REGEX = "[^ㄱ-ㅎㅏ-ㅣ가-힣\\w]+".toRegex()
    }
}
