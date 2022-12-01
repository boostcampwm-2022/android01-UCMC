package com.gta.presentation.ui.reservation.request

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.reservation.FinishReservationUseCase
import com.gta.domain.usecase.reservation.GetCarRentInfoUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import com.gta.domain.usecase.user.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
class ReservationRequestViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarRentInfoUseCase: GetCarRentInfoUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val finishReservationUseCase: FinishReservationUseCase,
    getReservationUseCase: GetReservationUseCase
) : ViewModel() {
    private val carId = args.get<String>("CAR_ID") ?: ""
    private val reservationId = args.get<String>("RESERVATION_ID") ?: ""
    val car: StateFlow<CarRentInfo>
    val reservation: StateFlow<Reservation>
    val user = MutableStateFlow(UserProfile())

    init {
        car =
            getCarRentInfoUseCase(carId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CarRentInfo()
            )

        reservation =
            getReservationUseCase(reservationId, carId).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Reservation()
            )

        reservation.onEach { reserve ->
            Timber.d(reserve.userId)
            getUserProfileUseCase(reserve.userId).onEach { profile ->
                user.emit(profile)
            }
        }.launchIn(viewModelScope)
    }

    private val _createReservationEvent = MutableSharedFlow<Boolean>()
    val createReservationEvent: SharedFlow<Boolean> get() = _createReservationEvent

    fun createReservation(accepted: Boolean) {
        val reservation = reservation.value
        val ownerId = car.value.ownerId
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
