package com.gta.domain.repository

import com.gta.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(uid: String): Flow<UserProfile>
    fun getNowReservation(uid: String): Flow<List<String>>
}
