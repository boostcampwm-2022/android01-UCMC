package com.gta.presentation.ui.returncar

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.UCMCResult
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentReturnCarBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.simpleReservation.collectLatest {
                viewModel.emitRemainTime()
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
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

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.returnCarEvent.collectLatest { state ->
                when (state) {
                    is UCMCResult.Success -> {
                        sendSnackBar(
                            message = getString(R.string.return_car_complete_msg)
                        )
                        findNavController().navigate(
                            ReturnCarFragmentDirections.actionReturnCarFragmentToReviewFragment(
                                reservationId = state.data
                            )
                        )
                    }
                    is UCMCResult.Error -> {
                        sendSnackBar(
                            message = getString(R.string.exception_not_found)
                        )
                    }
                }
            }
        }
    }
}
