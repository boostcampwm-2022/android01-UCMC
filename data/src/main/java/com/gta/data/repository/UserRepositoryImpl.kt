package com.gta.data.repository

import com.gta.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override fun getMyUserId(): String {
        return "(test)myId"
    }
}
