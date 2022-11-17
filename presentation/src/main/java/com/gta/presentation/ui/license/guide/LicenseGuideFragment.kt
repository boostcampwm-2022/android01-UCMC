package com.gta.presentation.ui.license.guide

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.CameraGuideFragment

class LicenseGuideFragment : CameraGuideFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title =
            getString(R.string.license_guide_toolbar)
        binding.tvCameraGuideTitle.setText(R.string.license_guide_title)
        binding.ivCameraGuideSample.setImageResource(R.drawable.img_driving_license)
    }

    override fun navigate(uri: String) {
        val direction = LicenseGuideFragmentDirections
            .actionLicenseGuideFragmentToLicenseRegistrationFragment(uri)
        findNavController().navigate(direction)
    }
}
