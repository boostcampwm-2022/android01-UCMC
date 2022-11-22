package com.gta.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.mypage.DeleteThumbnailUseCase
import com.gta.domain.usecase.mypage.SetThumbnailUseCase
import com.gta.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val setThumbnailUseCase: SetThumbnailUseCase,
    private val deleteThumbnailUseCase: DeleteThumbnailUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private val _thumbnailUpdateEvent = MutableSharedFlow<String?>()
    val thumbnailUpdateEvent: SharedFlow<String?> get() = _thumbnailUpdateEvent

    private val _nicknameEditEvent = MutableSharedFlow<String>()
    val nicknameEditEvent: SharedFlow<String> get() = _nicknameEditEvent

    private val uid = auth.currentUser?.uid

    init {
        getUserProfile()
    }

    fun changeThumbnail(uri: String?) {
        uri ?: return
        val previousImage = userProfile.value?.image ?: ""
        viewModelScope.launch {
            if (previousImage.isNotEmpty()) {
                deleteThumbnailUseCase(previousImage).first()
            }
            _userProfile.emit(userProfile.value?.copy(image = uri))
        }
    }

    private fun getUserProfile() {
        uid ?: return
        viewModelScope.launch {
            _userProfile.emit(getUserProfileUseCase(uid).first())
        }
    }

    fun updateThumbnail(uri: String) {
        uid ?: return
        viewModelScope.launch {
            _thumbnailUpdateEvent.emit(setThumbnailUseCase(uid, uri).first())
        }
    }

    fun navigateNicknameEdit() {
        viewModelScope.launch {
            _nicknameEditEvent.emit(userProfile.value?.image ?: "")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
