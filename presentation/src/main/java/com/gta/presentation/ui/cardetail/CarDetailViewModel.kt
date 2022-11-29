package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.GetUseStateAboutCarUseCase
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase,
    getUseStateAboutCarUseCase: GetUseStateAboutCarUseCase,
    private val chatClient: ChatClient
) : ViewModel() {

    val carInfo: StateFlow<CarDetail>
    val useState: StateFlow<UseState>

    private val carId = args.get<String>("CAR_ID") ?: "정보 없음"

    private val _navigateChattingEvent = MutableSharedFlow<String>()
    val navigateChattingEvent: SharedFlow<String> get() = _navigateChattingEvent

    init {

        carInfo = getCarDetailDataUseCase(carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarDetail()
        )

        useState = getUseStateAboutCarUseCase(FirebaseUtil.uid, carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UseState.UNAVAILABLE
        )
    }

    fun onChattingClick() {
        if (carId == "정보 없음" || useState.value == UseState.OWNER) return
        val cid = "${FirebaseUtil.uid}-$carId"
        checkChatChannel(cid)
    }

    private fun checkChatChannel(cid: String) {
        chatClient.queryChannel(
            "messaging",
            cid,
            QueryChannelRequest()
        ).enqueue { result ->
            if (result.isSuccess) {
                viewModelScope.launch {
                    _navigateChattingEvent.emit(result.data().cid)
                }
            } else {
                createChatChannel(cid)
            }
        }
    }

    private fun createChatChannel(cid: String) {
        chatClient.createChannel(
            channelType = "messaging",
            channelId = cid,
            memberIds = listOf(FirebaseUtil.uid, carInfo.value.owner.id),
            extraData = emptyMap()
        ).enqueue() { result ->
            if (result.isSuccess) {
                viewModelScope.launch {
                    _navigateChattingEvent.emit(result.data().cid)
                }
            } else {
                Timber.tag("chatting").i(result.error().message)
            }
        }
    }

    fun onReportClick() {
        Timber.d("자동차 상세페이지 신고")
    }
}
