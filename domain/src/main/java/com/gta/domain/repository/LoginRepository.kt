package com.gta.domain.repository

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun checkCurrentUser(uid: String): Flow<Boolean>
}
