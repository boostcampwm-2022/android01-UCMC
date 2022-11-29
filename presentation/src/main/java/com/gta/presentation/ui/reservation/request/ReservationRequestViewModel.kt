package com.gta.presentation.ui.reservation.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.Reservation
import com.gta.domain.usecase.reservation.CreateReservationUseCase
import com.gta.domain.usecase.reservation.GetCarRentInfoUseCase
import com.gta.domain.usecase.reservation.GetReservationUseCase
import com.gta.presentation.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationRequestViewModel @Inject constructor(
    getCarRentInfoUseCase: GetCarRentInfoUseCase,
    private val createReservationUseCase: CreateReservationUseCase,
    private val getReservationUseCase: GetReservationUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {
    var carId: String? = null

    private val _reservationDate = MutableLiveData<AvailableDate>()
    val reservationDate: LiveData<AvailableDate> get() = _reservationDate

    private val _insuranceOption = MutableLiveData<InsuranceOption>()
    val insuranceOption: LiveData<InsuranceOption> get() = _insuranceOption

    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    val car: StateFlow<CarRentInfo>? = carId?.let { carId ->
        getCarRentInfoUseCase(carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarRentInfo()
        )
    }

    private val _createReservationEvent = MutableSharedFlow<Boolean>()
    val createReservationEvent: SharedFlow<Boolean> get() = _createReservationEvent

    private val _getReservationEvent = MutableSharedFlow<Reservation>()
    val getReservationEvent: SharedFlow<Reservation> get() = _getReservationEvent

    val basePrice: LiveData<Int> = Transformations.map(_reservationDate) {
        val carPrice = car?.value?.price ?: 0
        DateUtil.getDateCount(it.start, it.end) * carPrice
    }

    private val _isPaymentOptionChecked = MutableStateFlow(false)
    val isPaymentOptionChecked: StateFlow<Boolean> get() = _isPaymentOptionChecked

    init {
        _totalPrice.addSource(basePrice) { basePrice ->
            val insurancePrice = insuranceOption.value?.price ?: 0
            _totalPrice.value = basePrice.plus(insurancePrice)
        }

        _totalPrice.addSource(insuranceOption) { option ->
            val price = basePrice.value ?: 0
            _totalPrice.value = price.plus(option.price)
        }
    }

    fun createReservation() {
        val userId = auth.currentUser?.uid ?: return
        val date = reservationDate.value ?: return
        val price = totalPrice.value ?: return
        val option = insuranceOption.value ?: return
        val ownerId = car?.value?.ownerId ?: ""

        carId?.let {
            viewModelScope.launch {
                _createReservationEvent.emit(
                    createReservationUseCase(
                        Reservation(
                            carId = it,
                            userId = userId,
                            reservationDate = date,
                            price = price,
                            insuranceOption = option.name
                        ),
                        ownerId
                    )
                )
            }
        }
    }

    fun getReservation(reservationId: String, carId: String) {
        viewModelScope.launch {
            _getReservationEvent.emit(getReservationUseCase(reservationId, carId))
        }
    }
}
