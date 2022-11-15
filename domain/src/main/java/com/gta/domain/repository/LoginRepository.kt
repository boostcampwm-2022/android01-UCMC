package com.gta.domain.repository

interface LoginRepository {
    fun checkCurrentUser(
        uid: String,
        onCompleted: ((Boolean) -> Unit)?
    )
}
