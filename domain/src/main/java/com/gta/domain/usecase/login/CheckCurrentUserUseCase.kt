package com.gta.domain.usecase.login

import com.gta.domain.model.LoginResult
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val updateUserMessageTokenUseCase: UpdateUserMessageTokenUseCase
) {
    suspend operator fun invoke(uid: String, shouldUpdateMessageToken: Boolean = false): Flow<LoginResult> {
        if (shouldUpdateMessageToken) {
            updateUserMessageTokenUseCase(uid)
        }
        return repository.checkCurrentUser(uid)
    }
}
