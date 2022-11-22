package com.gta.presentation.ui.mypage.mycars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCarsViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val getOwnerCarsUseCase: GetOwnerCarsUseCase
    ) :
    ViewModel() {
    private val _userCarList = MutableSharedFlow<List<SimpleCar>?>()
    val userCarList: SharedFlow<List<SimpleCar>?> get() = _userCarList

    private val uid = auth.currentUser?.uid

    fun getCarList() {
        uid ?: return
        Timber.d(uid)
        viewModelScope.launch {
            getOwnerCarsUseCase(uid).collectLatest {
                _userCarList.emit(it)
            }
        }
    }
}
