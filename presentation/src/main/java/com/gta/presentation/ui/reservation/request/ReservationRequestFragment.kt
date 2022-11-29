package com.gta.presentation.ui.reservation.request

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservationRequestFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation) {

    private val viewModel: ReservationRequestViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getReservationEvent.collect {
                    viewModel.carId = it.carId
                    when (it.insuranceOption) {
                        "LOW" -> binding.rgReservationInsuranceOption1.isChecked = true
                        "MEDIUM" -> binding.rgReservationInsuranceOption2.isChecked = true
                        "HIGH" -> binding.rgReservationInsuranceOption3.isChecked = true
                        else -> {}
                    }
                }
            }
        }
    }
}
