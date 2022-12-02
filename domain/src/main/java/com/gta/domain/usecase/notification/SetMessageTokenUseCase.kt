package com.gta.domain.usecase.notification

import com.gta.domain.repository.MessageTokenRepository
import javax.inject.Inject

class SetMessageTokenUseCase @Inject constructor(private val messageTokenRepository: MessageTokenRepository) {
    suspend operator fun invoke(string: String): Boolean = messageTokenRepository.setMessageToken(string)
}
