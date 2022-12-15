package com.gta.presentation.ui.cardetail

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gta.domain.model.CoolDownException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

                if (it.model != "정보 없음") {
                    viewModel.settingFinish()
                }
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
            viewModel.errorEvent.collectLatest { result ->
                if (result is UCMCResult.Error) {
                    when (result.e) {
                        is FirestoreException -> {
                            sendSnackBar(
                                message = getString(R.string.exception_load_data),
                                anchorView = binding.btnNext
                            )
                        }
                    }
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
                        sendSnackBar(
                            message = getString(R.string.report_success),
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
                    // 면허 있는지 확인
                    // TODO 로딩..
                    lifecycleScope.launch {
                        val myLicense = viewModel.getLicenseFromDatabaseUseCase(FirebaseUtil.uid)
                        if (myLicense is UCMCResult.Error && myLicense.e is Resources.NotFoundException || myLicense is UCMCResult.Error) {
                            sendSnackBar(
                                resources.getString(R.string.car_detail_no_license),
                                anchorView = binding.btnNext
                            )
                            return@launch
                        }

                        findNavController().navigate(
                            CarDetailFragmentDirections
                                .actionCarDetailFragmentToReservationFragment(
                                    viewModel.carInfo.value.id
                                )
                        )
                    }
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

    private fun handleErrorMessage(e: Exception) {
        when (e) {
            is FirestoreException -> {
                sendSnackBar(
                    message = getString(R.string.report_fail),
                    anchorView = binding.btnNext
                )
            }
            is CoolDownException -> {
                sendSnackBar(
                    message = getString(R.string.report_cooldown, e.cooldown),
                    anchorView = binding.btnNext
                )
            }
        }
    }
}
