package com.gta.presentation.ui.mypage.license

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.DrivingLicense
import com.gta.domain.usecase.license.GetLicenseFromDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageLicenseViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getLicenseFromDatabaseUseCase: GetLicenseFromDatabaseUseCase
) : ViewModel() {

    private val _drivingLicense = MutableStateFlow<DrivingLicense?>(null)
    val drivingLicense: StateFlow<DrivingLicense?> get() = _drivingLicense

    init {
        getLicense()
    }

    private fun getLicense() {
        val uid = auth.uid ?: return
        viewModelScope.launch {
            _drivingLicense.emit(getLicenseFromDatabaseUseCase(uid).first())
        }
    }
}

