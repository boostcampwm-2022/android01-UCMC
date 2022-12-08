package com.gta.domain.usecase.login

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(uid: String): UCMCResult<Unit> =
        repository.checkCurrentUser(uid)
}
