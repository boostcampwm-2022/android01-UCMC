package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCarDetailEditBinding
import com.gta.presentation.ui.base.BaseFragment

class CarDetailEditFragment : BaseFragment<FragmentCarDetailEditBinding>(
    R.layout.fragment_car_detail_edit
) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }
}
