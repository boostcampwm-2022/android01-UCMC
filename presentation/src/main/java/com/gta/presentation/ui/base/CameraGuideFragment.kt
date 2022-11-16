package com.gta.presentation.ui.base

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentCameraGuideBinding
import java.io.File

abstract class CameraGuideFragment : BaseFragment<FragmentCameraGuideBinding>(
    R.layout.fragment_camera_guide
) {

    abstract fun navigate(uri: String)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(photoUri)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSucceed ->
            if (isSucceed) {
                navigate(photoUri.toString())
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
            takePicture()
        }
    }

    private fun takePicture() {
        val permissionCheck = PermissionChecker.checkCallingOrSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
            cameraLauncher.launch(photoUri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
