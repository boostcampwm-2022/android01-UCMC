package com.gta.presentation.ui.nickname

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.NicknameState
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.nickname.CheckNicknameStateUseCase
import com.gta.domain.usecase.nickname.UpdateNicknameUseCase
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    args: SavedStateHandle,
    private val chatClient: ChatClient,
    private val checkNicknameStateUseCase: CheckNicknameStateUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {

    val thumb: StateFlow<String?> = MutableStateFlow(args.get<String>("thumb"))

    val nickname = MutableStateFlow("")

    private val _nicknameState = MutableStateFlow(NicknameState.IDLE)
    val nicknameState: StateFlow<NicknameState> get() = _nicknameState

    private val _nicknameChangeEvent = MutableEventFlow<UCMCResult<Unit>>()
    val nicknameChangeEvent get() = _nicknameChangeEvent.asEventFlow()

    fun checkNicknameState(nickname: String) {
        viewModelScope.launch {
            _nicknameState.emit(checkNicknameStateUseCase(nickname))
        }
    }

    fun updateNickname() {
        if (nicknameState.value != NicknameState.GREAT) return
        viewModelScope.launch {
            when (val result = updateNicknameUseCase(FirebaseUtil.uid, nickname.value)) {
                is UCMCResult.Error -> {
                    _nicknameChangeEvent.emit(result)
                }
                is UCMCResult.Success -> {
                    updateChatName()
                }
            }
        }
    }

    private fun updateChatName() {
        val currentUser = chatClient.getCurrentUser() ?: return
        val updatedUser = User(
            id = currentUser.id,
            name = nickname.value,
            image = currentUser.image
        )
        chatClient.updateUser(updatedUser).enqueue { result ->
            viewModelScope.launch {
                if (result.isSuccess) {
                    _nicknameChangeEvent.emit(UCMCResult.Success(Unit))
                } else {
                    _nicknameChangeEvent.emit(UCMCResult.Error(FirestoreException()))
                }
            }
        }
    }
}
