package com.gta.presentation.ui.nickname

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.NicknameState
import com.gta.domain.usecase.nickname.CheckNicknameStateUseCase
import com.gta.domain.usecase.nickname.UpdateNicknameUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    args: SavedStateHandle,
    private val checkNicknameStateUseCase: CheckNicknameStateUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase
) : ViewModel() {

    val thumb: StateFlow<String?> = MutableStateFlow(args.get<String>("thumb"))

    val nickname = MutableStateFlow("")

    private val _nicknameState = MutableStateFlow(NicknameState.IDLE)
    val nicknameState: StateFlow<NicknameState> get() = _nicknameState

    private val _nicknameChangeEvent = MutableSharedFlow<Boolean>()
    val nicknameChangeEvent: SharedFlow<Boolean> get() = _nicknameChangeEvent

    fun checkNicknameState(nickname: String) {
        viewModelScope.launch {
            _nicknameState.emit(checkNicknameStateUseCase(nickname))
        }
    }

    fun updateNickname() {
        if (nicknameState.value == NicknameState.GREAT) {
            viewModelScope.launch {
                _nicknameChangeEvent.emit(updateNicknameUseCase(FirebaseUtil.uid, nickname.value).first())
            }
        }
    }
}
