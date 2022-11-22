package com.gta.presentation.ui.pinkslip.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentPinkSlipRegistrationBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PinkSlipRegistrationFragment : BaseFragment<FragmentPinkSlipRegistrationBinding>(
    R.layout.fragment_pink_slip_registration
) {
    private val viewModel: PinkSlipRegistrationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        (requireActivity() as MainActivity).supportActionBar?.title =
            getString(R.string.pink_slip_registration_toolbar)
        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerEvent.collectLatest { state ->
                    findNavController().navigate(R.id.action_pinkSlipRegistrationFragment_to_myPageCarListFragment)
                }
            }
        }
    }
}
