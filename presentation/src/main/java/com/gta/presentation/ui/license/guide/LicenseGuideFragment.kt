package com.gta.presentation.ui.license.guide

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gta.presentation.ui.MainActivity
import com.gta.presentation.ui.base.CameraGuideFragment

class LicenseGuideFragment : CameraGuideFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.title = "운전자 등록"
    }

    override fun navigate(uri: String) {
        val direction = LicenseGuideFragmentDirections
            .actionLicenseGuideFragmentToLicenseRegistrationFragment(uri)
        findNavController().navigate(direction)
    }
}
