package com.gta.presentation.ui.mypage.license

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.DrivingLicense
import com.gta.domain.usecase.license.GetLicenseFromDatabaseUseCase
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageLicenseViewModel @Inject constructor(
    getLicenseFromDatabaseUseCase: GetLicenseFromDatabaseUseCase
) : ViewModel() {

    private val _drivingLicense = MutableStateFlow<DrivingLicense?>(null)
    val drivingLicense: StateFlow<DrivingLicense?> get() = _drivingLicense

    init {
        viewModelScope.launch {
            _drivingLicense.emit(getLicenseFromDatabaseUseCase(FirebaseUtil.uid).first())
        }
    }
}
