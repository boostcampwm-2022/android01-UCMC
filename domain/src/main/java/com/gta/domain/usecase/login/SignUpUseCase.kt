package com.gta.domain.usecase.login

import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(uid: String): UCMCResult<UserProfile> = repository.signUp(uid)
}
