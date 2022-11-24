package com.gta.data.repository

import com.gta.data.model.UserInfo
import com.gta.data.model.toProfile
import com.gta.data.source.UserDataSource
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override fun getUserProfile(uid: String): Flow<UserProfile> = callbackFlow {
        val profile = userDataSource.getUser(uid).first()?.toProfile(uid) ?: UserProfile()
        trySend(profile)
        awaitClose()
    }

    override fun getNowReservation(uid: String): Flow<String?> {
        return getUser(uid).map {
            it.rentedCar
        }
    }

    private fun getUser(uid: String): Flow<UserInfo> = callbackFlow {
        val userInfo = userDataSource.getUser(uid).first() ?: UserInfo()
        trySend(userInfo)
        awaitClose()
    }
}
