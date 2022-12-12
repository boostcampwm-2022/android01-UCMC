package com.gta.presentation.ui.reservation.check

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.CoolDownException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.InsuranceOption
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReservationCheckBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReservationCheckFragment :
    BaseFragment<FragmentReservationCheckBinding>(R.layout.fragment_reservation_check) {

    private val viewModel: ReservationCheckViewModel by viewModels()
    private var anchorView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.startCollect()

        binding.cvOwner.setOnClickListener {
            findNavController().navigate(
                ReservationCheckFragmentDirections
                    .actionReservationCheckFragmentToOwnerProfileFragment(
                        viewModel.user.value.id
                    )
            )
        }

        binding.inOwnerProfile.apply {
            tvChatting.setOnClickListener {
                viewModel.onChattingClick()
            }
            tvReport.setOnClickListener {
                viewModel.onReportClick()
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.navigateChattingEvent.collectLatest { cid ->
                findNavController().navigate(
                    ReservationCheckFragmentDirections.actionReservationCheckFragmentToChattingFragment(
                        cid
                    )
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reportEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Error -> {
                        handleErrorMessage(result.e)
                    }
                    is UCMCResult.Success -> {
                        sendSnackBar(
                            message = getString(R.string.report_success),
                            anchorView = anchorView

                        )
                    }
                }
            }
        }

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

                        if (result.data.state == ReservationState.PENDING.state && result.data.ownerId == FirebaseUtil.uid) {
                            anchorView = binding.btnReservationAccept
                            View.VISIBLE
                        } else {
                            anchorView = null
                            View.GONE
                        }.also { visibility ->
                            binding.btnReservationDecline.visibility = visibility
                            binding.btnReservationAccept.visibility = visibility
                        }
                    }
                    is UCMCResult.Error -> {
                        handleErrorMessage(result.e)
                    }
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.userEvent.collect { result ->
                if (result is UCMCResult.Error) {
                    handleErrorMessage(result.e)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.carEvent.collect { result ->
                if (result is UCMCResult.Error) {
                    handleErrorMessage(result.e)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.createReservationEvent.collect { result ->
                if (result is UCMCResult.Success) findNavController().popBackStack()
            }
        }
    }

    override fun onStop() {
        viewModel.stopCollect()
        super.onStop()
    }

    private fun handleErrorMessage(e: Exception) {
        val message =
            when (e) {
                is FirestoreException -> getString(R.string.report_fail)
                is CoolDownException -> getString(R.string.report_cooldown, e.cooldown)
                else -> e.message ?: getString(R.string.exception_not_found)
            }

        sendSnackBar(message = message, anchorView = anchorView)
    }
}
