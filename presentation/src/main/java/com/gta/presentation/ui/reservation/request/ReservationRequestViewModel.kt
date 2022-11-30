package com.gta.presentation.ui.reservation.request

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.usecase.reservation.FinishReservationUseCase
import com.gta.domain.usecase.reservation.GetCarRentInfoUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationRequestViewModel @Inject constructor(
    private val args: SavedStateHandle,
    private val getCarRentInfoUseCase: GetCarRentInfoUseCase,
    private val finishReservationUseCase: FinishReservationUseCase,
    private val getReservationUseCase: GetReservationUseCase
) : ViewModel() {
    private val carId by lazy { args.get<String>("CAR_ID") }
    private val reservationId by lazy { args.get<String>("RESERVATION_ID") }

    val car: StateFlow<CarRentInfo>? = carId?.let { carId ->
        getCarRentInfoUseCase(carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarRentInfo()
        )
    }

    val reservation: StateFlow<Reservation>? = reservationId?.let { reservationId ->
        carId?.let { carId ->
            getReservationUseCase(reservationId, carId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Reservation()
            )
        }
    }

    private val _createReservationEvent = MutableSharedFlow<Boolean>()
    val createReservationEvent: SharedFlow<Boolean> get() = _createReservationEvent

    fun createReservation(accepted: Boolean) {
        val reservation = reservation?.value ?: return
        val ownerId = car?.value?.ownerId ?: ""
        val state = if (accepted) ReservationState.ACCEPT else ReservationState.DECLINE

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
