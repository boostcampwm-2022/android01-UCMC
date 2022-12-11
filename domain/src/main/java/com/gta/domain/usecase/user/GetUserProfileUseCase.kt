package com.gta.domain.usecase.user

import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(uid: String): Flow<UCMCResult<UserProfile>> = repository.getUserProfile(uid)
}
