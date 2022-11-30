package com.gta.presentation.ui.returncar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReturnCarBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReturnCarFragment : BaseFragment<FragmentReturnCarBinding>(R.layout.fragment_return_car) {
    private val viewModel: ReturnCarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel

        val args: ReturnCarFragmentArgs by navArgs()

        binding.cvReturnBtn.setOnClickListener {
            val reservation = viewModel.simpleReservation.value ?: return@setOnClickListener
            findNavController().navigate(
                ReturnCarFragmentDirections.actionReturnCarFragmentToReviewFragment(
                    carId = args.carId,
                    reservationId = reservation.id
                )
            )
        }

        initCollector()
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.simpleReservation.collectLatest { reservation ->
                    if (reservation != null) {
                        viewModel.emitRemainTime()
                    }
                }
            }
        }
    }
}