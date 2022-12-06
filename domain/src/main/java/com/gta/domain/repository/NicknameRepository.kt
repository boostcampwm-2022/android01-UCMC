package com.gta.domain.repository

import com.gta.domain.model.NicknameState
import com.gta.domain.model.UCMCResult

interface NicknameRepository {
    fun checkNicknameState(nickname: String): NicknameState
    suspend fun updateNickname(uid: String, nickname: String): UCMCResult<Unit>
}
