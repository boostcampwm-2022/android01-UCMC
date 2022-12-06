package com.gta.presentation.ui.pinkslip.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.DuplicatedItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentPinkSlipRegistrationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PinkSlipRegistrationFragment : BaseFragment<FragmentPinkSlipRegistrationBinding>(
    R.layout.fragment_pink_slip_registration
) {
    private val viewModel: PinkSlipRegistrationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.registerEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Error -> {
                        handleErrorMessage(result.e)
                    }
                    is UCMCResult.Success -> {
                        sendSnackBar(getString(R.string.pink_slip_apply_success))
                        findNavController().navigate(R.id.action_pinkSlipRegistrationFragment_to_myPageCarListFragment)
                    }
                }
            }
        }
    }

    private fun handleErrorMessage(e: Exception) {
        when (e) {
            is DuplicatedItemException -> {
                sendSnackBar(
                    message = getString(R.string.pink_slip_error_duplicate),
                    anchorView = binding.btnPinkSlipRegistration
                )
            }
            is FirestoreException -> {
                sendSnackBar(
                    message = getString(R.string.pink_slip_error_firestore),
                    anchorView = binding.btnPinkSlipRegistration
                )
            }
        }
    }
}
