package com.gta.domain.usecase.nickname

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.NicknameRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: NicknameRepository
) {
    suspend operator fun invoke(uid: String, nickname: String): UCMCResult<Unit> =
        repository.updateNickname(uid, nickname)
}
