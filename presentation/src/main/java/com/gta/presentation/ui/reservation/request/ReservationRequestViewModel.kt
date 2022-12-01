package com.gta.presentation.ui.reservation.request

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationRequestViewModel @Inject constructor(
    args: SavedStateHandle,
    private val finishReservationUseCase: FinishReservationUseCase,
    getReservationUseCase: GetReservationUseCase,
    getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {
    private val carId = args.get<String>("CAR_ID") ?: ""
    private val reservationId = args.get<String>("RESERVATION_ID") ?: ""

    val car: StateFlow<CarDetail>
    val reservation: StateFlow<Reservation>

    private val _createReservationEvent = MutableSharedFlow<Boolean>()
    val createReservationEvent: SharedFlow<Boolean> get() = _createReservationEvent

    init {
        car = getCarDetailDataUseCase(carId)
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CarDetail()
            )

        reservation = getReservationUseCase(reservationId)
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = Reservation()
            )
    }

    fun createReservation(accepted: Boolean) {
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
