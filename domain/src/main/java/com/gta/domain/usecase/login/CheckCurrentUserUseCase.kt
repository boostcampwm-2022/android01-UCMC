package com.gta.domain.usecase.login

import com.gta.domain.repository.LoginRepository
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    fun invoke(
        uid: String,
        onCompleted: ((Boolean) -> Unit)? = null
    ) {
        repository.checkCurrentUser(uid, onCompleted)
    }
}
