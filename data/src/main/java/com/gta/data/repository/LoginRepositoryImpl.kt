package com.gta.data.repository

import com.gta.data.source.LoginDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource
) : LoginRepository {
    override fun checkCurrentUser(
        uid: String
    ): Flow<Boolean> = callbackFlow {
        val userInfo = userDataSource.getUser(uid).first()
        if (userInfo != null) {
            trySend(true)
        } else {
            trySend(loginDataSource.createUser(uid).first())
        }
        awaitClose()
    }
}
