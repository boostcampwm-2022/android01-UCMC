package com.gta.domain.repository

import com.gta.domain.model.NicknameState
import kotlinx.coroutines.flow.Flow

interface NicknameRepository {
    fun checkNicknameState(nickname: String): NicknameState
    fun updateNickname(uid: String, nickname: String): Flow<Boolean>
}
