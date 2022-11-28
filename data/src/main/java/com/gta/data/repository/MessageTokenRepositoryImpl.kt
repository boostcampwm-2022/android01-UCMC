package com.gta.data.repository

import com.gta.data.source.MessageTokenDataSource
import com.gta.domain.repository.MessageTokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageTokenRepositoryImpl @Inject constructor(private val messageTokenDataSource: MessageTokenDataSource) : MessageTokenRepository {
    override fun setMessageToken(token: String): Flow<Boolean> {
        return messageTokenDataSource.setMessageToken(token)
    }
}
