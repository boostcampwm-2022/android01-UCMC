package com.gta.presentation.ui.chatting.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.car.GetSimpleCarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(
    args: SavedStateHandle,
    private val getSimpleCarUseCase: GetSimpleCarUseCase
) : ViewModel() {

    private val _car = MutableStateFlow(SimpleCar())
    val car: StateFlow<SimpleCar> get() = _car

    private val _navigateCarDetailEvent = MutableSharedFlow<String>()
    val navigateCarDetailEvent: SharedFlow<String> get() = _navigateCarDetailEvent

    private val carId = args.get<String>("cid")?.substringAfterLast("-") ?: ""

    init {
        // cid는 "ChannelType:ChannelId"이 저장되어 있어요
        // ChannelId를 "대여자uid-차id"로 설정해줬기 때문에 ChannelId를 이용해 채팅 화면 상단에 차 정보를 보여줄 수 있습니다.
        viewModelScope.launch {
            _car.emit(getSimpleCarUseCase(carId).first())
        }
    }

    fun navigateCarDetail() {
        if (carId.isEmpty()) return
        viewModelScope.launch {
            _navigateCarDetailEvent.emit(carId)
        }
    }
}
