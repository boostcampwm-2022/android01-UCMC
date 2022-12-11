package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.CoolDownException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentOwnerProfileBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OwnerProfileFragment : BaseFragment<FragmentOwnerProfileBinding>(
    R.layout.fragment_owner_profile
) {

    private val viewModel: OwnerProfileViewModel by viewModels()
    private val carListAdapter by lazy { CarListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.startCollect()
        binding.rvCars.adapter = carListAdapter.apply {
            setItemClickListener(
                object : CarListAdapter.OnItemClickListener {
                    override fun onClick(id: String) {
                        findNavController().navigate(
                            OwnerProfileFragmentDirections
                                .actionOwnerProfileFragmentToCarDetailFragment(id)
                        )
                    }
                }
            )
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.owner.collectLatest {
                if (it.id == FirebaseUtil.uid) {
                    binding.tvReport.visibility = View.GONE
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reportEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Error -> {
                        handleErrorMessage(result.e)
                    }
                    is UCMCResult.Success -> {
                        sendSnackBar(message = getString(R.string.report_success))
                    }
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.carListEvent.collectLatest { result ->
                when (result) {
                    is UCMCResult.Success -> carListAdapter.submitList(result.data)
                    is UCMCResult.Error -> handleErrorMessage(result.e)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.errorEvent.collectLatest { result ->
                if (result is UCMCResult.Error) {
                    sendSnackBar(message = getString(R.string.exception_load_data))
                }
            }
        }
    }

    override fun onStop() {
        viewModel.stopCollect()
        super.onStop()
    }

    private fun handleErrorMessage(e: Exception) {
        when (e) {
            is FirestoreException -> {
                sendSnackBar(message = getString(R.string.report_fail))
            }
            is CoolDownException -> {
                sendSnackBar(message = getString(R.string.report_cooldown, e.cooldown))
            }
        }
    }
}
