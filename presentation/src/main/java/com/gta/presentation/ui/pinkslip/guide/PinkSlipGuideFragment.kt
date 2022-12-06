package com.gta.presentation.ui.pinkslip.guide

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.ui.base.CameraGuideFragment

class PinkSlipGuideFragment : CameraGuideFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCameraGuideTitle.setText(R.string.pink_slip_guide_title)
        binding.ivCameraGuideSample.setImageResource(R.drawable.img_pink_slip)
    }

    override fun navigate(uri: String) {
        val direction = PinkSlipGuideFragmentDirections
            .actionPinkSlipGuideFragmentToPinkSlipRegistrationFragment(uri)
        findNavController().navigate(direction)
    }
}
