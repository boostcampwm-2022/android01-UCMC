package com.gta.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.login.CheckCurrentUserUseCase
import com.gta.domain.usecase.login.SignUpUseCase
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val chatClient: ChatClient,
    private val checkCurrentUserUseCase: CheckCurrentUserUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _loginEvent = MutableEventFlow<UCMCResult<UserProfile>>()
    val loginEvent get() = _loginEvent.asEventFlow()

    var isLoading = true
        private set

    fun signInWithToken(token: String?) {
        token ?: return
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkCurrentUser()
            } else {
                Timber.e(task.exception)
            }
        }
    }

    fun checkCurrentUser() {
        val user = auth.currentUser
        if (user != null) {
            FirebaseUtil.setUid(user)
            viewModelScope.launch {
                handleLoginResult(checkCurrentUserUseCase(FirebaseUtil.uid))
            }
        } else {
            isLoading = false
        }
    }

    fun signUp() {
        viewModelScope.launch {
            handleLoginResult(signUpUseCase(FirebaseUtil.uid))
        }
    }

    private fun handleLoginResult(result: UCMCResult<UserProfile>) {
        viewModelScope.launch {
            if (result is UCMCResult.Success) {
                createChatUser(result.data)
            } else {
                isLoading = false
                _loginEvent.emit(result)
            }
        }
    }

    private fun createChatUser(profile: UserProfile) {
        viewModelScope.launch {
            val user = User(
                id = FirebaseUtil.uid,
                name = profile.name,
                image = profile.image ?: ""
            )
            chatClient.connectUser(
                user = user,
                token = chatClient.devToken(user.id)
            ).enqueue { result ->
                val loginResult = if (result.isSuccess) {
                    UCMCResult.Success(profile)
                } else {
                    UCMCResult.Error(FirestoreException())
                }
                viewModelScope.launch {
                    _loginEvent.emit(loginResult)
                }
            }
        }
    }
}
