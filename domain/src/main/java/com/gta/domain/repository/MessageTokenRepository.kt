package com.gta.domain.repository

import kotlinx.coroutines.flow.Flow

interface MessageTokenRepository {
    fun setMessageToken(token: String): Flow<Boolean>
}
