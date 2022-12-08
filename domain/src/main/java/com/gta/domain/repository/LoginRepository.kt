package com.gta.domain.repository

import com.gta.domain.model.UCMCResult

interface LoginRepository {
    suspend fun checkCurrentUser(uid: String): UCMCResult<Unit>
    suspend fun signUp(uid: String): UCMCResult<Unit>
    suspend fun updateUserMessageToken(uid: String): Boolean
}
