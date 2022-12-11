package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.GetUseStateAboutCarUseCase
import com.gta.domain.usecase.cardetail.UseState
import com.gta.domain.usecase.user.ReportUserUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase,
    getUseStateAboutCarUseCase: GetUseStateAboutCarUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val chatClient: ChatClient
) : ViewModel() {

    private val _errorEvent = MutableEventFlow<UCMCResult<Any>>()
    val errorEvent: EventFlow<UCMCResult<Any>>
        get() = _errorEvent

    val carInfo: StateFlow<CarDetail>
    val useState: StateFlow<UseState>

    private val carId = args.get<String>("CAR_ID") ?: "정보 없음"

    private val _navigateChattingEvent = MutableSharedFlow<String>()
    val navigateChattingEvent: SharedFlow<String> get() = _navigateChattingEvent

    private val _reportEvent = MutableEventFlow<UCMCResult<Unit>>()
    val reportEvent get() = _reportEvent.asEventFlow()

    init {
        carInfo = getCarDetailDataUseCase(carId).map {
            _errorEvent.emit(it)
            if (it is UCMCResult.Success) {
                it.data
            } else {
                CarDetail()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarDetail()
        )

        useState = getUseStateAboutCarUseCase(FirebaseUtil.uid, carId).map {
            _errorEvent.emit(it)
            if (it is UCMCResult.Success) {
                it.data
            } else {
                UseState.UNAVAILABLE
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UseState.UNAVAILABLE
        )
    }

    fun onChattingClick() {
        if (carId == "정보 없음" || useState.value == UseState.OWNER) return
        val cid = "${FirebaseUtil.uid}-$carId"
        createChatChannel(cid)
    }

    private fun createChatChannel(cid: String) {
        val result = chatClient.createChannel(
            channelType = "messaging",
            channelId = cid,
            memberIds = listOf(FirebaseUtil.uid, carInfo.value.owner.id),
            extraData = emptyMap()
        ).execute()

        if (result.isSuccess) {
            viewModelScope.launch {
                _navigateChattingEvent.emit(result.data().cid)
            }
        } else {
            Timber.tag("chatting").i(result.error().message)
        }
    }

    fun onReportClick() {
        if (carInfo.value.owner.id == "정보 없음" || FirebaseUtil.uid == carInfo.value.owner.id) {
            return
        }
        viewModelScope.launch {
            _reportEvent.emit(reportUserUseCase(carInfo.value.owner.id))
        }
    }
}
