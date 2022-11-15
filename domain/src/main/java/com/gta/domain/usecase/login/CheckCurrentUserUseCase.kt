package com.gta.domain.usecase.login

import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(uid: String): Flow<Boolean> = repository.checkCurrentUser(uid)
}
