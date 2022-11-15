package com.gta.presentation.ui.license

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentLicenseGuideBinding
import com.gta.presentation.ui.base.BaseFragment
import java.io.File

class LicenseGuideFragment : BaseFragment<FragmentLicenseGuideBinding>(
    R.layout.fragment_license_guide
) {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePicture()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSucceed ->
            if (isSucceed) {
                // photoUri
            }
        }

    private val photoFile by lazy {
        File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    private val photoUri by lazy {
        FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
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
        cameraLauncher.launch(photoUri)
    }
}
