package com.gta.data.repository

import com.gta.data.source.LoginDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.LoginRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource
) : LoginRepository {
    override fun checkCurrentUser(
        uid: String
    ): Flow<Boolean> = callbackFlow {
        userDataSource.getUser(uid).addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                trySend(true)
            } else {
                loginDataSource.createUser(uid).addOnCompleteListener { result ->
                    trySend(result.isSuccessful)
                }
            }
        }.addOnFailureListener {
            trySend(false)
        }
        awaitClose()
    }
}
