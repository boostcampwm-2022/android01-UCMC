package com.gta.domain.usecase.cardetail

import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOwnerInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(ownerId: String): Flow<UserProfile> {
        return userRepository.getUserProfile(ownerId)
    }
}
