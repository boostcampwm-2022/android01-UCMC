package com.gta.presentation.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.mypage.SetThumbnailUseCase
import com.gta.domain.usecase.user.GetUserProfileUseCase
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
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
    private val chatClient: ChatClient,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val setThumbnailUseCase: SetThumbnailUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> get() = _userProfile

    private val _thumbnailUpdateEvent = MutableEventFlow<UCMCResult<String>>()
    val thumbnailUpdateEvent get() = _thumbnailUpdateEvent.asEventFlow()

    private val _nicknameEditEvent = MutableSharedFlow<String>()
    val nicknameEditEvent: SharedFlow<String> get() = _nicknameEditEvent

    init {
        requestUserProfile()
    }

    private fun requestUserProfile() {
        viewModelScope.launch {
            _userProfile.emit(getUserProfileUseCase(FirebaseUtil.uid).first())
        }
    }

    fun updateThumbnail(uri: String) {
        val previousImage = userProfile.value.image
        viewModelScope.launch {
            val result = setThumbnailUseCase(FirebaseUtil.uid, uri, previousImage)
            if (result is UCMCResult.Success && result.data.isNotEmpty()) {
                updateChatThumbnail(result.data)
                _userProfile.emit(userProfile.value.copy(image = uri))
            }
            _thumbnailUpdateEvent.emit(result)
        }
    }

    fun navigateNicknameEdit() {
        viewModelScope.launch {
            _nicknameEditEvent.emit(userProfile.value.image)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun updateChatThumbnail(uri: String) {
        val currentUser = chatClient.getCurrentUser() ?: return
        val updatedUser = User(
            id = currentUser.id,
            name = currentUser.name,
            image = uri
        )
        chatClient.updateUser(updatedUser).enqueue()
    }
}
