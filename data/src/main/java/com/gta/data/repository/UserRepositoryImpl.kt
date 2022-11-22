package com.gta.data.repository

import com.gta.data.model.UserInfo
import com.gta.data.model.toProfile
import com.gta.data.source.UserDataSource
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override fun getUserProfile(uid: String): Flow<UserProfile> = callbackFlow {
        userDataSource.getUser(uid).addOnSuccessListener {
            if (it.exists()) {
                trySend(
                    it.toObject(UserInfo::class.java)?.toProfile(it.id) ?: UserProfile()
                )
            } else {
                trySend(UserProfile())
            }
        }.addOnFailureListener {
            Timber.d("실패")
            trySend(UserProfile())
        }
        awaitClose()
    }

    override fun getNowReservation(uid: String): Flow<Long?> {
        return getUser(uid).map {
            it.rentedCar
        }
    }

    private fun getUser(uid: String): Flow<UserInfo> = callbackFlow {
        userDataSource.getUser(uid).addOnSuccessListener { snapshot ->
            snapshot?.toObject(UserInfo::class.java)?.let {
                trySend(it)
            } ?: trySend(UserInfo())
        }.addOnFailureListener {
            trySend(UserInfo())
        }
        awaitClose()
    }
}
