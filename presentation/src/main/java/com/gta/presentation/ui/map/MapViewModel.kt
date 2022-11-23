package com.gta.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.LocationInfo
import com.gta.domain.model.SimpleCar
import com.gta.domain.usecase.map.GetAllCarsUseCase
import com.gta.domain.usecase.map.GetSearchAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllCarsUseCase: GetAllCarsUseCase,
    private val searchAddressUseCase: GetSearchAddressUseCase
) :
    ViewModel() {
    private val SEARCH_TIMEOUT = 1000L

    private var _cars = MutableStateFlow<List<SimpleCar>>(emptyList())
    val cars: StateFlow<List<SimpleCar>> get() = _cars

    private var _selectCar = MutableStateFlow(SimpleCar())
    val selectCar: StateFlow<SimpleCar> get() = _selectCar

    val queryFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var _searchResult = MutableStateFlow<List<LocationInfo>>(emptyList())
    val searchResult: StateFlow<List<LocationInfo>> get() = _searchResult

    private var _searchStringResult = MutableStateFlow<List<String>>(emptyList())
    val searchStringResult: StateFlow<List<String>> get() = _searchStringResult

    init {
        getAllCars()

        queryFlow
            .debounce(SEARCH_TIMEOUT)
            .flatMapLatest { query ->
                searchAddressUseCase(query)
            }
            .flowOn(Dispatchers.IO)
            .onEach { rawList ->
                _searchResult.emit(rawList)
                rawList.map { info ->
                    info.address
                }.also { mappedList ->
                    _searchStringResult.emit(mappedList)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getAllCars() {
        viewModelScope.launch {
            getAllCarsUseCase().collectLatest {
                _cars.emit(it)
            }
        }
    }

    fun emitSelected(car: SimpleCar) {
        viewModelScope.launch {
            _selectCar.emit(car)
        }
    }
}
