package com.gta.presentation.ui.license.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.ExpiredItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentLicenseRegistrationBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LicenseRegistrationFragment : BaseFragment<FragmentLicenseRegistrationBinding>(
    R.layout.fragment_license_registration
) {
    private val viewModel: LicenseRegistrationViewModel by viewModels()

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
                        sendSnackBar(getString(R.string.license_registration_apply_success))
                        findNavController().navigate(R.id.action_licenseRegistrationFragment_to_myPageLicenseFragment)
                    }
                }
            }
        }
    }

    private fun handleErrorMessage(e: Exception) {
        when (e) {
            is ExpiredItemException -> {
                sendSnackBar(
                    message = getString(R.string.license_registration_error_expired),
                    anchorView = binding.btnLicenseRegistration
                )
            }
            is FirestoreException -> {
                sendSnackBar(
                    message = getString(R.string.license_registration_error_firestore),
                    anchorView = binding.btnLicenseRegistration
                )
            }
        }
    }
}
