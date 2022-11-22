package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.domain.model.AvailableDate
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {
    private val viewModel: ReservationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.car?.collectLatest { car ->
                    car?.let { setupDatePicker(it.availableDate) }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createReservationEvent.collectLatest { state ->
                    if (state) {
                        // 결제하기
                    }
                }
            }
        }
    }

    private fun setupDatePicker(availableDate: AvailableDate) {
        val (startDate, endDate) = availableDate

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(startDate to endDate, null))
            .setStart(startDate)
            .setEnd(endDate)
            .build()

        val datePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTheme(R.style.Theme_UCMC_DatePicker)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setReservationDate(AvailableDate(it.first, it.second))
        }

        binding.ivReservationNext.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }
    }
}
