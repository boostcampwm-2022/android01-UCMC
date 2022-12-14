package com.gta.domain.repository

import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile

interface LoginRepository {
    suspend fun checkCurrentUser(uid: String): UCMCResult<UserProfile>
    suspend fun signUp(uid: String): UCMCResult<UserProfile>
    suspend fun updateUserMessageToken(uid: String): Boolean
}
