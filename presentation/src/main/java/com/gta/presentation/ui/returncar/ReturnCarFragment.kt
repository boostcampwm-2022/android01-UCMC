package com.gta.presentation.ui.returncar

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
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

    private val remainTimeMsgFormat by lazy { getString(R.string.return_car_title) }
    private val primaryColor by lazy {
        ContextCompat.getColor(
            requireContext(),
            R.color.primaryColor
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel

        binding.cvReturnBtn.setOnClickListener {
            val reservation = viewModel.simpleReservation.value ?: return@setOnClickListener
            findNavController().navigate(
                ReturnCarFragmentDirections.actionReturnCarFragmentToReviewFragment(
                    reservationId = reservation.reservationId
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.remainTime.collectLatest { remainTime ->
                    val text = String.format(remainTimeMsgFormat, remainTime)
                    val spannable = SpannableString(text).apply {
                        setSpan(
                            ForegroundColorSpan(primaryColor),
                            5,
                            5 + remainTime.length,
                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                        )
                    }
                    binding.tvReturnRemainTime.text = spannable
                }
            }
        }
    }
}