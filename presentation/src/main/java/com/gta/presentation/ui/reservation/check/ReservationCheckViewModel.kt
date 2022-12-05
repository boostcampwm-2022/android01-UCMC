package com.gta.presentation.ui.reservation.check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.reservation.FinishReservationUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ReservationCheckViewModel @Inject constructor(
    args: SavedStateHandle,
    private val finishReservationUseCase: FinishReservationUseCase,
    private val getReservationUseCase: GetReservationUseCase,
    private val getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {
    private val reservationId = args.get<String>("RESERVATION_ID") ?: "정보 없음"

    private val _carEvent = MutableEventFlow<UCMCResult<CarDetail>>()
    val carEvent: EventFlow<UCMCResult<CarDetail>> get() = _carEvent.asEventFlow()

    private val _car = MutableStateFlow(CarDetail())
    val car: StateFlow<CarDetail> get() = _car

    private val _reservationEvent = MutableEventFlow<UCMCResult<Reservation>>()
    val reservationEvent: EventFlow<UCMCResult<Reservation>> get() = _reservationEvent.asEventFlow()

    private val _reservation = MutableStateFlow(Reservation())
    val reservation: StateFlow<Reservation> get() = _reservation

    private val _createReservationEvent = MutableEventFlow<Boolean>()
    val createReservationEvent: EventFlow<Boolean> get() = _createReservationEvent.asEventFlow()

    private lateinit var collectJob: CompletableJob

    @OptIn(ExperimentalCoroutinesApi::class)
    fun startCollect() {
        collectJob = SupervisorJob()

        getReservationUseCase(reservationId)
            .flowOn(Dispatchers.IO)
            .onEach {
                _reservationEvent.emit(it)
            }.launchIn(viewModelScope + collectJob)

        reservation.flatMapLatest { target ->
            getCarDetailDataUseCase(target.carId)
        }.flowOn(Dispatchers.IO)
            .onEach {
                _carEvent.emit(it)
            }.launchIn(viewModelScope + collectJob)
    }

    fun stopCollect() {
        collectJob.cancel()
    }

    fun finishReservation(accepted: Boolean) {
        val reservation = reservation.value
        val ownerId = car.value.owner.id
        val state = if (accepted) ReservationState.ACCEPT else ReservationState.CANCEL

        viewModelScope.launch {
            _createReservationEvent.emit(
                finishReservationUseCase(
                    accepted,
                    reservation.copy(state = state.string),
                    ownerId
                )
            )
        }
    }
}
