package com.gta.presentation.ui.license.registration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.DrivingLicense
import com.gta.domain.usecase.license.GetLicenseUseCase
import com.gta.domain.usecase.license.SetLicenseUseCase
import com.gta.presentation.util.ImageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseRegistrationViewModel @Inject constructor(
    private val imageUtil: ImageUtil,
    private val auth: FirebaseAuth,
    private val getLicenseUseCase: GetLicenseUseCase,
    private val setLicenseUseCase: SetLicenseUseCase,
    args: SavedStateHandle
) : ViewModel() {

    private val _drivingLicense = MutableStateFlow<DrivingLicense?>(null)
    val drivingLicense: StateFlow<DrivingLicense?> get() = _drivingLicense

    private val _registerEvent = MutableSharedFlow<Boolean>()
    val registerEvent: SharedFlow<Boolean> get() = _registerEvent

    init {
        args.get<String>("uri")?.let { uri ->
            getLicense(uri)
        }
    }

    private fun getLicense(uri: String) {
        val buffer = imageUtil.getByteBuffer(uri) ?: return
        viewModelScope.launch {
            _drivingLicense.emit(getLicenseUseCase(buffer).first())
        }
    }

    fun registerLicense() {
        val license = drivingLicense.value ?: return
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _registerEvent.emit(setLicenseUseCase(uid, license).first())
        }
    }
}
