package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.toKotlinPair
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.model.InsuranceLevel
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {
    private val args: ReservationFragmentArgs by navArgs()
    private val carInfo by lazy { args.carInfo }

    private val viewModel: ReservationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setupDatePicker()

        setupRadioGroup()

        return binding.run {
            vm = viewModel
            carInfo = this@ReservationFragment.carInfo
            root
        }
    }

    private fun setupRadioGroup() {
        binding.rgReservationInsuranceOptions.setOnCheckedChangeListener { _, isChecked ->
            val price = carInfo.price + when (isChecked) {
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
            viewModel.selectedInsuranceOption.value = isChecked to price
        }
    }

    private fun setupDatePicker() {
        // 임시 불가능한 날짜들
        val invalidateDates = listOf(1669248000000 to 1669420800000)

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(carInfo.reservationRange, invalidateDates))
            .setStart(carInfo.reservationRange.first)
            .setEnd(carInfo.reservationRange.second)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.selectedDateRange.value = it.toKotlinPair()
        }

        binding.ivReservationNext.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }
    }
}

// 임시 자동차 객체
@Parcelize
data class TmpCarInfo(
    val id: String,
    val title: String,
    val location: String,
    val carType: String,
    val price: Int,
    val comment: String,
    val images: List<String>,
    val reservationRange: Pair<Long, Long>
) : Parcelable
