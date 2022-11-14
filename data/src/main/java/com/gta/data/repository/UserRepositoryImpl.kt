package com.gta.data.repository

import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepositoryImpl : UserRepository {
    override fun getMyUserId(): String {
        return "myUserId"
    }

    override fun getUserProfile(userId: String): Flow<UserProfile> {
        return MutableStateFlow(UserProfile(userId, "(test)선구자", 25, null))
    }
}
