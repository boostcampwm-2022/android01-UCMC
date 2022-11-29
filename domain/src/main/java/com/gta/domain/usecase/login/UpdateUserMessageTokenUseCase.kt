package com.gta.domain.usecase.login

import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class UpdateUserMessageTokenUseCase @Inject constructor(private val loginRepository: LoginRepository) {
    suspend operator fun invoke(uid: String): Boolean = loginRepository.updateUserMessageToken(uid)
}
