package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.GetOwnerInfoUseCase
import com.gta.domain.usecase.user.ReportUserUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    args: SavedStateHandle,
    getOwnerInfoUseCase: GetOwnerInfoUseCase,
    getOwnerCarsUseCase: GetOwnerCarsUseCase,
    private val reportUserUseCase: ReportUserUseCase
) : ViewModel() {

    private val _errorEvent = MutableEventFlow<UCMCResult<Any>>()
    val errorEvent: EventFlow<UCMCResult<Any>>
        get() = _errorEvent.asEventFlow()

    val owner: StateFlow<UserProfile>

    /*
    private val _carListEvent = MutableEventFlow<UCMCResult<List<SimpleCar>>>()
    val carListEvent: EventFlow<UCMCResult<List<SimpleCar>>> get() = _carListEvent.asEventFlow()
     */
    val carList: SharedFlow<UCMCResult<List<SimpleCar>>>

    private val _reportEvent = MutableEventFlow<UCMCResult<Unit>>()
    val reportEvent get() = _reportEvent.asEventFlow()

    private val ownerId = args.get<String>("OWNER_ID") ?: "정보 없음"

    // private lateinit var collectJob: CompletableJob

    init {
        /*
            현재 상태에서는 eventFlow의 장점을 사용하고 있지 못하다
            왜냐하면 Fragment가 박살나면 viewmodel의 collect가 멈추기 때문에
            flow에서 방출되는 값을 받지 못하기 때문이다.
            그렇기 때문에 에초에 eventFlow 장점을 살리지 못한다면 viewModel의 collect를 제거하는것이
            좋다고 판단하였다.
            현재 코드나 그전 코드나 동작 방식은 같다.
     */
        carList = getOwnerCarsUseCase(ownerId)
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000L))

        owner = getOwnerInfoUseCase(ownerId).map {
            _errorEvent.emit(it)
            if (it is UCMCResult.Success) {
                it.data
            } else {
                UserProfile(image = null)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile(image = null)
        )
    }

    /*
    fun startCollect() {
        collectJob = SupervisorJob()

        getOwnerCarsUseCase(ownerId).onEach {
            _carListEvent.emit(it)
        }.launchIn(viewModelScope + collectJob)
    }

    fun stopCollect() {
        collectJob.cancel()
    }
     */

    fun onReportClick() {
        if (ownerId == "정보 없음" || FirebaseUtil.uid == ownerId) {
            return
        }
        viewModelScope.launch {
            _reportEvent.emit(reportUserUseCase(ownerId))
        }
    }
}
