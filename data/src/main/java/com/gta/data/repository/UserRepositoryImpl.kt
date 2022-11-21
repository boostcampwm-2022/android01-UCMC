package com.gta.data.repository

import com.gta.data.model.UserInfo
import com.gta.data.model.toProfile
import com.gta.data.source.UserDataSource
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override fun getMyUserId(): String {
        return "(test)myId"
    }

    override fun getUserProfile(uid: String): Flow<UserProfile> = callbackFlow {
        userDataSource.getUser(uid).addOnSuccessListener {
            if (it.exists()) {
                trySend(
                    it.toObject(UserInfo::class.java)?.toProfile(it.id) ?: UserProfile()
                )
            }
        }.addOnFailureListener {
            Timber.d("실패")
        }
        awaitClose()
    }
}
