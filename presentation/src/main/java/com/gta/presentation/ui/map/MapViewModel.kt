package com.gta.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.map.GetAllCarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val getAllCarsUseCase: GetAllCarsUseCase) :
    ViewModel() {
    private var _cars = MutableSharedFlow<List<SimpleCar>>()
    val cars: SharedFlow<List<SimpleCar>> get() = _cars

    private var _selectCar = MutableStateFlow(SimpleCar())
    val selectCar: StateFlow<SimpleCar> get() = _selectCar

    fun getAllCars() {
        viewModelScope.launch {
            getAllCarsUseCase().first() {
                _cars.emit(it)
                true
            }
        }
    }

    fun emitSelected(car: SimpleCar) {
        viewModelScope.launch {
            _selectCar.emit(car)
        }
    }
}
