package com.gta.presentation.ui.reservation.check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UserProfile
import com.gta.domain.usecase.car.GetSimpleCarUseCase
import com.gta.domain.usecase.reservation.FinishReservationUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import com.gta.domain.usecase.user.GetUserProfileUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.FirebaseUtil
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
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
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
    private val getSimpleCarUseCase: GetSimpleCarUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {
    private val reservationId = args.get<String>("RESERVATION_ID") ?: "정보 없음"

    private val _carEvent = MutableEventFlow<UCMCResult<SimpleCar>>()
    val carEvent: EventFlow<UCMCResult<SimpleCar>> get() = _carEvent.asEventFlow()

    private val _car = MutableStateFlow(SimpleCar())
    val car: StateFlow<SimpleCar> get() = _car

    private val _userEvent = MutableEventFlow<UCMCResult<UserProfile>>()
    val userEvent: EventFlow<UCMCResult<UserProfile>> get() = _userEvent.asEventFlow()

    private val _user = MutableStateFlow(UserProfile())
    val user: StateFlow<UserProfile> get() = _user

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
                if (it is UCMCResult.Success) {
                    _reservation.emit(it.data)
                }
                _reservationEvent.emit(it)
            }.launchIn(viewModelScope + collectJob)

        reservationEvent.flatMapLatest { target ->
            when (target) {
                is UCMCResult.Success -> {
                    getSimpleCarUseCase(target.data.carId)
                }
                is UCMCResult.Error -> {
                    flow { UCMCResult.Error(Exception()) }
                }
            }
        }.onEach {
            _car.emit(it)
            if (it == SimpleCar()) {
                _carEvent.emit(UCMCResult.Error(FirestoreException()))
            } else {
                _carEvent.emit(UCMCResult.Success(it))
            }
        }.launchIn(viewModelScope + collectJob)

        reservationEvent.flatMapLatest { target ->
            when (target) {
                is UCMCResult.Success -> {
                    if (FirebaseUtil.uid == target.data.ownerId) {
                        getUserProfileUseCase(target.data.lenderId)
                    } else {
                        getUserProfileUseCase(target.data.ownerId)
                    }
                }
                is UCMCResult.Error -> {
                    flow { UCMCResult.Error(Exception()) }
                }
            }
        }.onEach {
            _user.emit(it)
            if (it == UserProfile()) {
                _userEvent.emit(UCMCResult.Error(FirestoreException()))
            } else {
                _userEvent.emit(UCMCResult.Success(it))
            }

        }.launchIn(viewModelScope + collectJob)
    }

    fun stopCollect() {
        collectJob.cancel()
    }

    fun finishReservation(accepted: Boolean) {
        val reservation = reservation.value
        val ownerId = reservation.ownerId
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
