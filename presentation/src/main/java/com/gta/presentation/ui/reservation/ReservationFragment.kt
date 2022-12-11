package com.gta.presentation.ui.reservation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.toPair
import com.gta.domain.model.toPairList
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.DateValidator
import com.gta.presentation.util.repeatOnStarted
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

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.car.collectLatest { car ->
                setupDatePicker(car.availableDate, car.reservationDates)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.getCarRentInfoEvent.collectLatest { state ->
                binding.cpiReservationProgress.isVisible = false
                if (state is UCMCResult.Error) {
                    sendSnackBar(
                        message = getString(R.string.exception_not_found)
                    )
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.createReservationEvent.collectLatest { state ->
                when (state) {
                    is UCMCResult.Success -> {
                        findNavController().navigate(ReservationFragmentDirections.actionReservationFragmentToPaymentFragment())
                    }
                    is UCMCResult.Error -> {
                        sendSnackBar(
                            message = getString(R.string.exception_not_found)
                        )
                    }
                }
            }
        }
    }


    private fun setupDatePicker(
        availableDate: AvailableDate,
        reservationDates: List<AvailableDate>
    ) {
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
        binding.rgReservationInsuranceOptions.setOnCheckedChangeListener { _, checkedId ->
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
