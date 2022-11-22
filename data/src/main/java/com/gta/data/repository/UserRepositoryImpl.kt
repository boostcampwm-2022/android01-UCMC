package com.gta.data.repository

import com.gta.data.model.UserInfo
import com.gta.data.source.UserDataSource
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource
) : UserRepository {
    override fun getMyUserId(): String {
        return "(test)myId"
    }

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        dataSource.getUser(uid).addOnSuccessListener { snapshot ->
            snapshot.toObject(UserInfo::class.java)?.let { userInfo ->
                trySend(getUserProfile(uid, userInfo))
            } ?: trySend(null)
        }.addOnFailureListener {
            trySend(null)
        }
        awaitClose()
    }

    private fun getUserProfile(uid: String, userInfo: UserInfo): UserProfile =
        UserProfile(
            id = uid,
            name = userInfo.nickname,
            temp = userInfo.temperature,
            image = userInfo.icon
        )
}
