package com.gta.presentation.ui.reservation.check

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.ReservationState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationRequestBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationCheckFragment :
    BaseFragment<FragmentReservationRequestBinding>(R.layout.fragment_reservation_request) {

    private val viewModel: ReservationCheckViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.startCollect()

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reservation?.collect {
                when (it.insuranceOption) {
                    InsuranceOption.LOW.name -> binding.rgReservationInsuranceOption1
                    InsuranceOption.MEDIUM.name -> binding.rgReservationInsuranceOption2
                    InsuranceOption.HIGH.name -> binding.rgReservationInsuranceOption3
                    else -> null
                }?.isChecked = true

                (if (it.state == ReservationState.PENDING.state) View.VISIBLE else View.GONE).also { visibility ->
                    binding.btnReservationDecline.visibility = visibility
                    binding.btnReservationAccept.visibility = visibility
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.createReservationEvent.collect {
                if (it) findNavController().popBackStack()
            }
        }
    }

    override fun onStop() {
        viewModel.stopCollect()
        super.onStop()
    }
}
