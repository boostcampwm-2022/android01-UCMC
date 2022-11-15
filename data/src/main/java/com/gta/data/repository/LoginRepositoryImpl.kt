package com.gta.data.repository

import com.gta.data.source.LoginDataSource
import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val dataSource: LoginDataSource
) : LoginRepository {
    override fun checkCurrentUser(
        uid: String,
        onCompleted: ((Boolean) -> Unit)?
    ) {
        dataSource.getUser(uid).addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                onCompleted?.invoke(true)
            } else {
                createUser(uid, onCompleted)
            }
        }.addOnFailureListener {
            onCompleted?.invoke(false)
        }
    }

    private fun createUser(uid: String, onCompleted: ((Boolean) -> Unit)?) {
        dataSource.createUser(uid).addOnCompleteListener { result ->
            onCompleted?.invoke(result.isSuccessful)
        }
    }
}
