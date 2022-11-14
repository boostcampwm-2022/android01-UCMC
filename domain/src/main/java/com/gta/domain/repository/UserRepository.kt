package com.gta.domain.repository

import com.gta.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getMyUserId(): String
    fun getUserProfile(userId: String): Flow<UserProfile>
}