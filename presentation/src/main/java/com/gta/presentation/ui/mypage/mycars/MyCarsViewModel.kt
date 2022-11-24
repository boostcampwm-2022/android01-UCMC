package com.gta.presentation.ui.mypage.mycars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.RemoveCarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCarsViewModel @Inject constructor(
    auth: FirebaseAuth,
    private val getOwnerCarsUseCase: GetOwnerCarsUseCase,
    private val removeCarUseCase: RemoveCarUseCase
) :
    ViewModel() {
    private val _userCarList = MutableStateFlow<List<SimpleCar>>(emptyList())
    val userCarList: StateFlow<List<SimpleCar>> get() = _userCarList

    private val uid = auth.currentUser?.uid

    init {
        getCarList()
    }

    private fun getCarList() {
        uid ?: return
        viewModelScope.launch {
            getOwnerCarsUseCase(uid).collectLatest {
                _userCarList.emit(it)
            }
        }
    }

    fun deleteCar(carId: String) {
        uid ?: return
        viewModelScope.launch {
            removeCarUseCase(uid, carId).collectLatest {
                getCarList()
            }
        }
    }
}
