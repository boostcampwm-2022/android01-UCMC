package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailBinding
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.BaseFragment
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.carInfo.collectLatest {
                    (requireActivity() as MainActivity).supportActionBar?.title = it.licensePlate
                    pagerAdapter.submitList(it.images)
                    binding.indicatorImages.attachTo(binding.vpImages)
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
                    // TODO 반납하기 페이지
                }
                else -> {}
            }
        }
    }
}
