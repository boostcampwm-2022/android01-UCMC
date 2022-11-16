package com.gta.presentation.ui.license.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentLicenseRegistrationBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LicenseRegistrationFragment : BaseFragment<FragmentLicenseRegistrationBinding>(
    R.layout.fragment_license_registration
) {
    private val navArgs: LicenseRegistrationFragmentArgs by navArgs()
    private val viewModel: LicenseRegistrationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        (requireActivity() as MainActivity).supportActionBar?.title = "운전면허 확인"
        Glide.with(this)
            .load(navArgs.uri)
            .into(binding.ivLicenseRegistrationResult)
        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerEvent.collectLatest { state ->
                    Timber.tag("License").i("$state")
                }
            }
        }
    }
}
