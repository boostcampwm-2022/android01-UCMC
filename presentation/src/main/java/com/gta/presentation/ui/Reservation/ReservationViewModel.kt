package com.gta.presentation.ui.reservation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gta.presentation.R
import com.gta.presentation.model.InsuranceLevel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ReservationViewModel @AssistedInject constructor(@Assisted private val carInfo: TmpCarInfo) :
    ViewModel() {
    val selectedDateRange = MutableLiveData<Pair<Long, Long>>()
    val selectedInsuranceOption = MutableLiveData<Int>()
    val totalPrice = MediatorLiveData<Int>()

    private var dateCount = 0

    init {
        totalPrice.addSource(selectedDateRange) { range ->
            dateCount = getDateCount(range.first, range.second)

            val insurancePrice = selectedInsuranceOption.value ?: 0
            totalPrice.value = (dateCount * carInfo.price).plus(insurancePrice)
        }

        totalPrice.addSource(selectedInsuranceOption) { option ->
            totalPrice.value = (dateCount * carInfo.price).plus(getOptionPrice(option))
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

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(carInfo: TmpCarInfo): ReservationViewModel
    }

    companion object {
        private const val DAY_TIME_UNIT = 86400000L

        fun getDateCount(startDate: Long, endDate: Long) =
            (endDate - startDate).div(DAY_TIME_UNIT).plus(1).toInt()

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

