package com.gta.presentation.ui.reservation.check

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationRequestBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReservationCheckFragment :
    BaseFragment<FragmentReservationRequestBinding>(R.layout.fragment_reservation_request) {

    private val viewModel: ReservationCheckViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.startCollect()

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reservationEvent.collect { result ->
                when (result) {
                    is UCMCResult.Success -> {
                        when (result.data.insuranceOption) {
                            InsuranceOption.LOW.name -> binding.rgReservationInsuranceOption1
                            InsuranceOption.MEDIUM.name -> binding.rgReservationInsuranceOption2
                            InsuranceOption.HIGH.name -> binding.rgReservationInsuranceOption3
                            else -> null
                        }?.isChecked = true

                        (if (result.data.state == ReservationState.PENDING.state) View.VISIBLE else View.GONE).also { visibility ->
                            binding.btnReservationDecline.visibility = visibility
                            binding.btnReservationAccept.visibility = visibility
                        }
                    }
                    is UCMCResult.Error -> {
                        sendSnackBar(resources.getString(R.string.exception_load_data))
                    }
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.createReservationEvent.collectLatest {
                if (it) findNavController().popBackStack()
            }
        }
    }

    override fun onStop() {
        viewModel.stopCollect()
        super.onStop()
    }
}
