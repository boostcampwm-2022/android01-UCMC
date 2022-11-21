package com.gta.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val useCase: GetUserProfileUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _userProfile.emit(useCase(uid).first())
        }
    }
}
