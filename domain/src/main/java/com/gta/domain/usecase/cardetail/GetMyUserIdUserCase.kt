package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.UserRepository
import javax.inject.Inject

class GetMyUserIdUserCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): String {
        return userRepository.getMyUserId()
    }
}
