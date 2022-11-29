package com.gta.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _changeAuthStateEvent = MutableSharedFlow<Boolean>()
    val changeAuthStateEvent: SharedFlow<Boolean> get() = _changeAuthStateEvent

    private val authStateListener by lazy {
        FirebaseAuth.AuthStateListener {
            viewModelScope.launch {
                _changeAuthStateEvent.emit(it.currentUser == null)
            }
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}
