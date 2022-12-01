package com.gta.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gta.domain.model.LoginResult
import com.gta.domain.usecase.login.CheckCurrentUserUseCase
import com.gta.domain.usecase.login.SignUpUseCase
import com.gta.domain.usecase.user.GetUserProfileUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val chatClient: ChatClient,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val checkCurrentUserUseCase: CheckCurrentUserUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginResult>()
    val loginEvent: SharedFlow<LoginResult> get() = _loginEvent

    fun signInWithToken(token: String?) {
        token ?: return
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkCurrentUser(shouldUpdateMessageToken = true)
            } else {
                Timber.e(task.exception)
            }
        }
    }

    fun checkCurrentUser(shouldUpdateMessageToken: Boolean = false) {
        val user = auth.currentUser ?: return
        FirebaseUtil.setUid(user)
        viewModelScope.launch {
            handleLoginResult(checkCurrentUserUseCase(FirebaseUtil.uid, shouldUpdateMessageToken).first())
        }
    }

    fun signUp() {
        viewModelScope.launch {
            handleLoginResult(signUpUseCase(FirebaseUtil.uid).first())
        }
    }

    private fun handleLoginResult(loginResult: LoginResult) {
        viewModelScope.launch {
            if (loginResult == LoginResult.SUCCESS) {
                createChatUser()
            } else {
                _loginEvent.emit(loginResult)
            }
        }
    }

    private fun createChatUser() {
        viewModelScope.launch {
            val profile = getUserProfileUseCase(FirebaseUtil.uid).first()
            val user = User(
                id = FirebaseUtil.uid,
                name = profile.name,
                image = profile.image
            )
            chatClient.connectUser(
                user = user,
                token = chatClient.devToken(user.id)
            ).enqueue { result ->
                val loginResult = if (result.isSuccess) LoginResult.SUCCESS else LoginResult.FAILURE
                viewModelScope.launch {
                    _loginEvent.emit(loginResult)
                }
            }
        }
    }
}
