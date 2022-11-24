package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.toPair
import com.gta.domain.model.toPairList
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

        setUpRadioGroup()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.car?.collectLatest { car ->
                    setupDatePicker(car.availableDate, car.reservationDates)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createReservationEvent.collectLatest { state ->
                    findNavController().navigate(ReservationFragmentDirections.actionReservationFragmentToPaymentFragment())
                }
            }
        }
    }

    private fun setupDatePicker(availableDate: AvailableDate, reservationDates: List<AvailableDate>) {
        val (startDate, endDate) = availableDate

        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidator(availableDate.toPair(), reservationDates.toPairList()))
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

    private fun setUpRadioGroup() {
        binding.rgReservationInsuranceOptions.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rg_reservation_insurance_option_1 -> {
                    viewModel.setInsuranceOption(InsuranceOption.LOW)
                }
                R.id.rg_reservation_insurance_option_2 -> {
                    viewModel.setInsuranceOption(InsuranceOption.MEDIUM)
                }
                R.id.rg_reservation_insurance_option_3 -> {
                    viewModel.setInsuranceOption(InsuranceOption.HIGH)
                }
            }
        }
    }
}
