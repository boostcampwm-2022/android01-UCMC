package com.gta.presentation.ui.mypage.mycars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.GetOwnerCarsUseCase
import com.gta.domain.usecase.cardetail.RemoveCarUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.FirebaseUtil
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCarsViewModel @Inject constructor(
    private val getOwnerCarsUseCase: GetOwnerCarsUseCase,
    private val removeCarUseCase: RemoveCarUseCase
) :
    ViewModel() {

    private val _deleteEvent = MutableEventFlow<UCMCResult<Unit>>()
    val deleteEvent: EventFlow<UCMCResult<Unit>> get() = _deleteEvent.asEventFlow()

    private val _userCarEvent = MutableEventFlow<UCMCResult<List<SimpleCar>>>()
    val userCarEvent: EventFlow<UCMCResult<List<SimpleCar>>> get() = _userCarEvent.asEventFlow()

    private val uid = FirebaseUtil.uid

    init {
        getCarList()
    }

    fun getCarList() {
        viewModelScope.launch {
            getOwnerCarsUseCase(uid).collectLatest { result ->
                _userCarEvent.emit(result)
            }
        }
    }

    fun deleteCar(carId: String) {
        viewModelScope.launch {
            _deleteEvent.emit(removeCarUseCase(uid, carId))
            getCarList()
        }
    }
}
