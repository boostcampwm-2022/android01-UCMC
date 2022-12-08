package com.gta.domain.usecase.login

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val updateUserMessageTokenUseCase: UpdateUserMessageTokenUseCase
) {
    suspend operator fun invoke(uid: String, shouldUpdateMessageToken: Boolean = false): UCMCResult<Unit> {
        if (shouldUpdateMessageToken) {
            updateUserMessageTokenUseCase(uid)
        }
        return repository.checkCurrentUser(uid)
    }
}
