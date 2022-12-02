package com.gta.data.repository

import com.gta.data.source.LoginDataSource
import com.gta.data.source.MessageTokenDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.LoginResult
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource,
    private val messageTokenDataSource: MessageTokenDataSource
) : LoginRepository {
    override fun checkCurrentUser(
        uid: String
    ): Flow<LoginResult> = callbackFlow {
        val userInfo = userDataSource.getUser(uid).first()
        if (userInfo != null) {
            trySend(LoginResult.SUCCESS)
        } else {
            trySend(LoginResult.NEWUSER)
        }
        awaitClose()
    }

    override fun signUp(uid: String) = callbackFlow {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        val created = loginDataSource.createUser(uid, messageToken).first()
        if (created) {
            trySend(LoginResult.SUCCESS)
        } else {
            trySend(LoginResult.FAILURE)
        }
        awaitClose()
    }

    override suspend fun updateUserMessageToken(uid: String): Boolean {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        return userDataSource.updateUserMessageToken(uid, messageToken).first()
    }
}
