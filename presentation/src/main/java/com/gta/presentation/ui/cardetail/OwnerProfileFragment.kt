package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentOwnerProfileBinding
import com.gta.presentation.ui.base.BaseFragment

class OwnerProfileFragment : BaseFragment<FragmentOwnerProfileBinding>(
    R.layout.fragment_owner_profile
) {

    private val viewModel: OwnerProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.rvCars.adapter = CarListAdapter()
        return binding.root
    }
}
