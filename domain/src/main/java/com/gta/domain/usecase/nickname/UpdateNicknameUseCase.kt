package com.gta.domain.usecase.nickname

import com.gta.domain.repository.NicknameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: NicknameRepository
) {
    operator fun invoke(uid: String, nickname: String): Flow<Boolean> =
        repository.updateNickname(uid, nickname)
}
