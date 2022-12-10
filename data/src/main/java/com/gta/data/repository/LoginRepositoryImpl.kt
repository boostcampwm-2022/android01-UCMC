package com.gta.data.repository

import android.content.res.Resources.NotFoundException
import com.gta.data.source.LoginDataSource
import com.gta.data.source.MessageTokenDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource,
    private val messageTokenDataSource: MessageTokenDataSource
) : LoginRepository {
    override suspend fun checkCurrentUser(
        uid: String
    ): UCMCResult<Unit> =
        userDataSource.getUser(uid).first()?.let {
            UCMCResult.Success(Unit)
        } ?: UCMCResult.Error(NotFoundException())


    override suspend fun signUp(uid: String): UCMCResult<Unit> =
        userDataSource.getUser(uid).first()?.let {
            UCMCResult.Success(Unit)
        } ?: createUser(uid)

    override suspend fun updateUserMessageToken(uid: String): Boolean {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        return userDataSource.updateUserMessageToken(uid, messageToken).first()
    }

    private suspend fun createUser(uid: String): UCMCResult<Unit> {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        val created = loginDataSource.createUser(uid, messageToken).first()
        return if (created) {
            UCMCResult.Success(Unit)
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
