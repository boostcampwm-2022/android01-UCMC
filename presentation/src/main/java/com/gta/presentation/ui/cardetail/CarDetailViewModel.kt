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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _carInfoEvent = MutableEventFlow<UCMCResult<CarDetail>>()
    val carInfoEvent: EventFlow<UCMCResult<CarDetail>> get() = _carInfoEvent.asEventFlow()

    private val _carInfo = MutableStateFlow(CarDetail())
    val carInfo: StateFlow<CarDetail> get() = _carInfo

    val useState: StateFlow<UseState>

    private val carId = args.get<String>("CAR_ID") ?: "정보 없음"

    private val _navigateChattingEvent = MutableSharedFlow<String>()
    val navigateChattingEvent: SharedFlow<String> get() = _navigateChattingEvent

    private val _reportEvent = MutableEventFlow<UCMCResult<Unit>>()
    val reportEvent get() = _reportEvent.asEventFlow()

    init {
        getCarDetailDataUseCase(carId).onEach {
            if (it is UCMCResult.Success)
                _carInfo.emit(it.data)
            _carInfoEvent.emit(it)
        }.launchIn(viewModelScope)

        useState = getUseStateAboutCarUseCase(FirebaseUtil.uid, carId).stateIn(
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
