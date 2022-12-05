package com.gta.presentation.ui.license.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.license.GetLicenseFromImageUseCase
import com.gta.domain.usecase.license.SetLicenseUseCase
import com.gta.presentation.util.ImageUtil
import com.gta.presentation.util.MutableEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseRegistrationViewModel @Inject constructor(
    private val imageUtil: ImageUtil,
    private val auth: FirebaseAuth,
    private val getLicenseFromImageUseCase: GetLicenseFromImageUseCase,
    private val setLicenseUseCase: SetLicenseUseCase,
    args: SavedStateHandle
) : ViewModel() {

    private val _drivingLicense = MutableStateFlow<DrivingLicense?>(null)
    val drivingLicense: StateFlow<DrivingLicense?> get() = _drivingLicense

    private val _registerEvent = MutableEventFlow<UCMCResult<Unit>>()
    val registerEvent get() = _registerEvent

    private val _licensePicture = MutableStateFlow("")
    val licensePicture: StateFlow<String> get() = _licensePicture

    init {
        args.get<String>("uri")?.let { uri ->
            viewModelScope.launch {
                _licensePicture.emit(uri)
            }
            getLicense(uri)
        }
    }

    private fun getLicense(uri: String) {
        val buffer = imageUtil.getByteBuffer(uri) ?: return
        viewModelScope.launch {
            _drivingLicense.emit(getLicenseFromImageUseCase(buffer))
        }
    }

    fun registerLicense() {
        val license = drivingLicense.value ?: return
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _registerEvent.emit(setLicenseUseCase(uid, license, licensePicture.value))
        }
    }
}
