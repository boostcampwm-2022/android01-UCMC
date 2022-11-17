package com.gta.presentation.ui.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gta.domain.model.AvailableDate
import com.gta.presentation.R
import com.gta.presentation.model.InsuranceLevel
import com.gta.presentation.util.DateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ReservationViewModel @AssistedInject constructor(@Assisted private val carInfo: TmpCarInfo) :
    ViewModel() {
    private val _reservationDate = MutableLiveData<AvailableDate>()
    val reservationDate: LiveData<AvailableDate> = _reservationDate

    val selectedInsuranceOption = MutableLiveData<Int>()

    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    val basePrice: LiveData<Int> = Transformations.map(_reservationDate) {
        DateUtil.getDateCount(it.start, it.end) * carInfo.price
    }

    init {
        _totalPrice.addSource(basePrice) { basePrice ->
            val insurancePrice = selectedInsuranceOption.value?.let { getOptionPrice(it) } ?: 0
            _totalPrice.value = basePrice.plus(insurancePrice)
        }

        _totalPrice.addSource(selectedInsuranceOption) { option ->
            val price = basePrice.value ?: 0
            _totalPrice.value = price.plus(getOptionPrice(option))
        }
    }

    private fun getOptionPrice(option: Int): Int {
        return when (option) {
            R.id.rg_reservation_insurance_option_1 -> {
                InsuranceLevel.LOW.price
            }
            R.id.rg_reservation_insurance_option_2 -> {
                InsuranceLevel.MEDIUM.price
            }
            R.id.rg_reservation_insurance_option_3 -> {
                InsuranceLevel.HIGH.price
            }
            else -> 0
        }
    }

    fun setReservationDate(selected: AvailableDate) {
        _reservationDate.value = selected
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(carInfo: TmpCarInfo): ReservationViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            carInfo: TmpCarInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(carInfo) as T
            }
        }
    }
}
