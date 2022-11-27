package com.gta.domain.usecase.notification

import com.gta.domain.repository.MessageTokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetMessageTokenUseCase @Inject constructor(private val messageTokenRepository: MessageTokenRepository) {
    operator fun invoke(string: String): Flow<Boolean> = messageTokenRepository.setMessageToken(string)
}