package com.gta.data.repository

import com.gta.data.source.NicknameDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.NicknameState
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.NicknameRepository
import kotlinx.coroutines.flow.first
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

    override suspend fun updateNickname(uid: String, nickname: String): UCMCResult<Unit> =
        if (dataSource.updateNickname(uid, nickname).first()) {
            UCMCResult.Success(Unit)
        } else {
            UCMCResult.Error(FirestoreException())
        }

    companion object {
        private const val MIN_LENGTH = 2
        private val SYMBOL_REGEX = "[^ㄱ-ㅎㅏ-ㅣ가-힣\\w]+".toRegex()
    }
}
