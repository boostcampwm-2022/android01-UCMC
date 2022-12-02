package com.gta.presentation.ui.mypage.license

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentMypageLicenseBinding
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyPageLicenseFragment :
    BaseFragment<FragmentMypageLicenseBinding>(R.layout.fragment_mypage_license) {

    private val viewModel: MyPageLicenseViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        binding.btnLicenseRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_myPageLicenseFragment_to_licenseGuideFragment)
        }
        initCollector()
    }

    private fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.drivingLicense.collectLatest {
                if (it == null) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.btnLicenseRegistration.text =
                        resources.getString(R.string.license_registration_button)
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.btnLicenseRegistration.text =
                        resources.getString(R.string.mypage_re_register)
                }
            }
        }
    }
}
