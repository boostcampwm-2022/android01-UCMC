package com.gta.domain.repository

import com.gta.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun checkCurrentUser(uid: String): Flow<LoginResult>
    fun signUp(uid: String): Flow<LoginResult>
}
