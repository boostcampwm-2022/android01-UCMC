package com.gta.presentation.ui.reservation.check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.reservation.FinishReservationUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
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

    var car: StateFlow<CarDetail>? = null
    var reservation: StateFlow<Reservation>? = null

    private val _createReservationEvent = MutableSharedFlow<Boolean>()
    val createReservationEvent: SharedFlow<Boolean> get() = _createReservationEvent

    private lateinit var collectJob: CompletableJob

    @OptIn(ExperimentalCoroutinesApi::class)
    fun startCollect() {
        collectJob = SupervisorJob()

        reservation = getReservationUseCase(reservationId)
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = viewModelScope + collectJob,
                started = SharingStarted.Eagerly,
                initialValue = Reservation()
            )

        car = reservation
            ?.flatMapLatest { target ->
                getCarDetailDataUseCase(target.carId)
            }?.flowOn(Dispatchers.IO)
            ?.stateIn(
                scope = viewModelScope + collectJob,
                started = SharingStarted.Eagerly,
                initialValue = CarDetail()
            )
    }

    fun stopCollect() {
        collectJob.cancel()
    }

    fun finishReservation(accepted: Boolean) {
        val reservation = reservation?.value ?: return
        val ownerId = car?.value?.owner?.id ?: return
        val state = if (accepted) ReservationState.ACCEPT else ReservationState.CANCEL

        viewModelScope.launch {
            _createReservationEvent.emit(
                finishReservationUseCase(
                    accepted,
                    reservation.copy(state = state.state),
                    ownerId
                )
            )
        }
    }
}
