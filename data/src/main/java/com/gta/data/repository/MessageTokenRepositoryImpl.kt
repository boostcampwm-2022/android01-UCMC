package com.gta.data.repository

import com.gta.data.source.MessageTokenDataSource
import com.gta.domain.repository.MessageTokenRepository
import javax.inject.Inject

class MessageTokenRepositoryImpl @Inject constructor(private val messageTokenDataSource: MessageTokenDataSource) : MessageTokenRepository {
    override suspend fun setMessageToken(token: String): Boolean {
        return messageTokenDataSource.setMessageToken(token)
    }
}
