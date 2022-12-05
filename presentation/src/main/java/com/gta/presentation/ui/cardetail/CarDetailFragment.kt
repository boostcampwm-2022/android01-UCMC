package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailBinding
import com.gta.presentation.model.ReportEventState
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CarDetailFragment : BaseFragment<FragmentCarDetailBinding>(
    R.layout.fragment_car_detail
) {

    private val viewModel: CarDetailViewModel by viewModels()
    private val pagerAdapter by lazy { CarImagePagerAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.vm = viewModel
        binding.vpImages.adapter = pagerAdapter
        binding.indicatorImages.attachTo(binding.vpImages)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.carInfo.collectLatest {
                (requireActivity() as MainActivity).supportActionBar?.title = it.licensePlate
                pagerAdapter.submitList(it.images)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.navigateChattingEvent.collectLatest { cid ->
                findNavController().navigate(
                    CarDetailFragmentDirections.actionCarDetailFragmentToChattingFragment(cid)
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reportEvent.collectLatest { result ->
                when (result) {
                    is ReportEventState.Success -> {
                        sendSnackBar(
                            message = getString(R.string.report_success),
                            anchorView = binding.btnNext
                        )
                    }
                    is ReportEventState.Cooldown -> {
                        sendSnackBar(
                            message = getString(R.string.report_cooldown, result.cooldown),
                            anchorView = binding.btnNext
                        )
                    }
                    is ReportEventState.Error -> {
                        sendSnackBar(
                            message = getString(R.string.report_fail),
                            anchorView = binding.btnNext
                        )
                    }
                }
            }
        }

        binding.cvOwner.setOnClickListener {
            findNavController().navigate(
                CarDetailFragmentDirections
                    .actionCarDetailFragmentToOwnerProfileFragment(
                        viewModel.carInfo.value.owner.id
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

        binding.btnNext.setOnClickListener {
            when (viewModel.useState.value) {
                UseState.OWNER -> {
                    findNavController().navigate(
                        CarDetailFragmentDirections
                            .actionCarDetailFragmentToCarDetailEditFragment(
                                viewModel.carInfo.value.id
                            )
                    )
                }
                UseState.USER -> {
                    findNavController().navigate(
                        CarDetailFragmentDirections
                            .actionCarDetailFragmentToReservationFragment(
                                viewModel.carInfo.value.id
                            )
                    )
                }
                UseState.NOW_RENT_USER -> {
                    findNavController().navigate(
                        CarDetailFragmentDirections.actionCarDetailFragmentToReturnCarFragment(
                            viewModel.carInfo.value.id
                        )
                    )
                }
                else -> {}
            }
        }
    }
}
