package com.gta.presentation.ui.reservation.request

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationRequestBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationRequestFragment :
    BaseFragment<FragmentReservationRequestBinding>(R.layout.fragment_reservation_request) {

    private val viewModel: ReservationRequestViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reservation?.collect {
                when (it.insuranceOption) {
                    "LOW" -> binding.rgReservationInsuranceOption1.isChecked = true
                    "MEDIUM" -> binding.rgReservationInsuranceOption2.isChecked = true
                    "HIGH" -> binding.rgReservationInsuranceOption3.isChecked = true
                    else -> {}
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.createReservationEvent.collect {
                if (it) findNavController().popBackStack()
            }
        }
    }
}
