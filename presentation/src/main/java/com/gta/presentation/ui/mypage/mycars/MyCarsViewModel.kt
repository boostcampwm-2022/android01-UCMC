package com.gta.presentation.ui.mypage.mycars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.RemoveCarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCarsViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val getOwnerCarsUseCase: GetOwnerCarsUseCase,
    private val removeCarUseCase: RemoveCarUseCase
) :
    ViewModel() {
    private val _userCarList = MutableSharedFlow<List<SimpleCar>?>()
    val userCarList: SharedFlow<List<SimpleCar>?> get() = _userCarList

    private val _carRemoved = MutableSharedFlow<Boolean>()
    val carRemoved: SharedFlow<Boolean> get() = _carRemoved

    private val uid = auth.currentUser?.uid

    fun getCarList() {
        uid ?: return
        viewModelScope.launch {
            getOwnerCarsUseCase(uid).first() {
                _userCarList.emit(it)
                true
            }
        }
    }

    fun deleteCar(carId: String) {
        uid ?: return
        viewModelScope.launch {
            removeCarUseCase(uid, carId).first() {
                _carRemoved.emit(it)
                getCarList()
                true
            }
        }
    }
}
