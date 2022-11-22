package com.gta.domain.usecase.nickname

import com.gta.domain.model.NicknameState
import com.gta.domain.repository.NicknameRepository
import javax.inject.Inject

class CheckNicknameStateUseCase @Inject constructor(
    private val repository: NicknameRepository
) {
    operator fun invoke(nickname: String): NicknameState =
        repository.checkNicknameState(nickname)
}
