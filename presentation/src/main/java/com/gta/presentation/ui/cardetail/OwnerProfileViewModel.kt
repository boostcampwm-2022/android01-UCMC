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
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    args: SavedStateHandle,
    getOwnerInfoUseCase: GetOwnerInfoUseCase,
    private val getOwnerCarsUseCase: GetOwnerCarsUseCase,
    private val reportUserUseCase: ReportUserUseCase
) : ViewModel() {

    val owner: StateFlow<UserProfile>

    private val _carListEvent = MutableEventFlow<UCMCResult<List<SimpleCar>>>()
    val carListEvent: EventFlow<UCMCResult<List<SimpleCar>>> get() = _carListEvent.asEventFlow()

    private val _reportEvent = MutableEventFlow<UCMCResult<Unit>>()
    val reportEvent get() = _reportEvent.asEventFlow()

    private val ownerId = args.get<String>("OWNER_ID") ?: "정보 없음"

    private lateinit var collectJob: CompletableJob

    init {
        owner = getOwnerInfoUseCase(ownerId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile(image = null)
        )
    }

    fun startCollect() {
        collectJob = SupervisorJob()

        getOwnerCarsUseCase(ownerId).onEach {
            _carListEvent.emit(it)
        }.launchIn(viewModelScope + collectJob)
    }

    fun stopCollect() {
        collectJob.cancel()
    }

    fun onReportClick() {
        if (ownerId == "정보 없음" || FirebaseUtil.uid == ownerId) {
            return
        }
        viewModelScope.launch {
            _reportEvent.emit(reportUserUseCase(ownerId))
        }
    }
}
