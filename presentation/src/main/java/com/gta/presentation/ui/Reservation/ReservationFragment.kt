package com.gta.presentation.ui.reservation

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import timber.log.Timber

class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {

    override fun onStart() {
        super.onStart()

        // 임시 대여 가능 날짜
        val validRange = Pair(1668438000000, 1669593600000)
        // 임시 불가능한 날짜들
        val invalidateDates = listOf(1669248000000 to 1669420800000)

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(validRange, invalidateDates))
            .setStart(validRange.first)
            .setEnd(validRange.second)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            Timber.d(it.toString())
        }

        binding.ivReservationNext.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }
    }
}