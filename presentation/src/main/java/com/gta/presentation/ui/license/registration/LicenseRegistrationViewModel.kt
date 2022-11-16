package com.gta.presentation.ui.license.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gta.domain.usecase.license.GetLicenseUseCase
import com.gta.domain.usecase.license.SetLicenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LicenseRegistrationViewModel @Inject constructor(
    private val getLicenseUseCase: GetLicenseUseCase,
    private val setLicenseUseCase: SetLicenseUseCase,
    args: SavedStateHandle
) : ViewModel() {
    init {
        val str = args.get<String>("uri")
        Timber.tag("License").i("$str")
    }
}
