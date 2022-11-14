package com.gta.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginEvent = MutableSharedFlow<Boolean>()
    val loginEvent: SharedFlow<Boolean> get() = _loginEvent

    fun checkLoginState() {
        emitLoginEvent(auth.currentUser != null)
    }

    fun signinWithToken(token: String?) {
        token ?: return
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emitLoginEvent(true)
            } else {
                Timber.e(task.exception)
            }
        }
    }

    private fun emitLoginEvent(state: Boolean) {
        viewModelScope.launch {
            _loginEvent.emit(state)
        }
    }
}
