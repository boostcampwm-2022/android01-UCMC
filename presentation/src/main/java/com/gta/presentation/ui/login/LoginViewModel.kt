package com.gta.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gta.domain.model.LoginResult
import com.gta.domain.usecase.login.CheckCurrentUserUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val useCase: CheckCurrentUserUseCase
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<LoginResult>()
    val loginEvent: SharedFlow<LoginResult> get() = _loginEvent

    private val _termsEvent = MutableSharedFlow<Boolean>()
    val termsEvent: SharedFlow<Boolean> get() = _termsEvent

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
        val user = auth.currentUser ?: return
        FirebaseUtil.setUid(user)
        viewModelScope.launch {
            _loginEvent.emit(useCase(FirebaseUtil.uid).first())
        }
    }

    fun checkTermsAccepted(value: Boolean) {
        viewModelScope.launch {
            _termsEvent.emit(value)
        }
    }
}
