package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailBinding
import com.gta.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarDetailFragment : BaseFragment<FragmentCarDetailBinding>(
    R.layout.fragment_car_detail
) {

    private val viewModel: CarDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvOwner.setOnClickListener {
            findNavController().navigate(
                CarDetailFragmentDirections
                    .actionCarDetailFragmentToOwnerProfileFragment(
                        viewModel.carInfo.value.owner.id
                    )
            )
        }
    }
}
