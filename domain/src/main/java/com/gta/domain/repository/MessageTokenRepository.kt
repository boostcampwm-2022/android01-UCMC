package com.gta.domain.repository

interface MessageTokenRepository {
    suspend fun setMessageToken(token: String): Boolean
}
