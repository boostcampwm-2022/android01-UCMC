package com.gta.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private val _loginEvent = MutableSharedFlow<Boolean>()
    val loginEvent: SharedFlow<Boolean> get() = _loginEvent

    fun signinWithToken(token: String?) {
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
}
