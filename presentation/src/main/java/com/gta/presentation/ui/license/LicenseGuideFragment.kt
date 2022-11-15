package com.gta.presentation.ui.license

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentLicenseGuideBinding
import com.gta.presentation.ui.base.BaseFragment

class LicenseGuideFragment : BaseFragment<FragmentLicenseGuideBinding>(
    R.layout.fragment_license_guide
) {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePicture()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLicenseGuidePicture.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        val permissionCheck = checkCallingOrSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (permissionCheck == PERMISSION_GRANTED) {
            takePicture()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePicture() {

    }
}
