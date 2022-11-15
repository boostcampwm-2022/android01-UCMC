package com.gta.data.repository

import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepositoryImpl : UserRepository {
    override fun getMyUserId(): String {
        return "(test)myId"
    }

    override fun getUserProfile(userId: String): Flow<UserProfile> {
        return MutableStateFlow(UserProfile(userId, "(test)$userId", 36.5F, null))
    }
}
