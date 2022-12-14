package com.gta.data.repository

import android.content.res.Resources.NotFoundException
import com.gta.data.model.toProfile
import com.gta.data.source.LoginDataSource
import com.gta.data.source.MessageTokenDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource,
    private val messageTokenDataSource: MessageTokenDataSource
) : LoginRepository {

    override suspend fun checkCurrentUser(uid: String): UCMCResult<UserProfile> =
        userDataSource.getUser(uid).first()?.let { user ->
            UCMCResult.Success(user.toProfile(uid))
        } ?: UCMCResult.Error(NotFoundException())

    override suspend fun signUp(uid: String): UCMCResult<UserProfile> =
        userDataSource.getUser(uid).first()?.let { user ->
            UCMCResult.Success(user.toProfile(uid))
        } ?: createUser(uid)

    override suspend fun updateUserMessageToken(uid: String): Boolean {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        return userDataSource.updateUserMessageToken(uid, messageToken).first()
    }

    private suspend fun createUser(uid: String): UCMCResult<UserProfile> {
        val messageToken = messageTokenDataSource.getMessageToken().first()
        return loginDataSource.createUser(uid, messageToken).first()?.let { userInfo ->
            UCMCResult.Success(userInfo.toProfile(uid))
        } ?: UCMCResult.Error(FirestoreException())
    }
}
