package com.gta.presentation.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.Coordinate
import com.gta.domain.model.LocationInfo
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.map.GetNearCarsUseCase
import com.gta.domain.usecase.map.GetSearchAddressUseCase
import com.gta.presentation.util.EventFlow
import com.gta.presentation.util.MutableEventFlow
import com.gta.presentation.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearCarsUseCase: GetNearCarsUseCase,
    private val searchAddressUseCase: GetSearchAddressUseCase
) : ViewModel() {
    private val SEARCH_TIMEOUT = 500L

    private var _carsRequest = MutableSharedFlow<Pair<Coordinate, Coordinate>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val carsRequest: SharedFlow<Pair<Coordinate, Coordinate>> get() = _carsRequest

    private var _carsResponse = MutableEventFlow<UCMCResult<List<SimpleCar>>>()
    val carsResponse: EventFlow<UCMCResult<List<SimpleCar>>> get() = _carsResponse.asEventFlow()

    private var _searchRequest = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val searchRequest: SharedFlow<String> get() = _searchRequest

    private var _searchResponse = MutableEventFlow<UCMCResult<List<LocationInfo>>>()
    val searchResponse: EventFlow<UCMCResult<List<LocationInfo>>> get() = _searchResponse.asEventFlow()

    private var _selectCar = MutableStateFlow(SimpleCar())
    val selectCar: StateFlow<SimpleCar> get() = _selectCar

    private lateinit var collectJob: CompletableJob

    fun startCollect() {
        collectJob = SupervisorJob()

        searchRequest
            .debounce(SEARCH_TIMEOUT)
            .flatMapLatest { query ->
                searchAddressUseCase(query)
            }.flowOn(Dispatchers.IO)
            .onEach { result ->
                _searchResponse.emit(result)
            }.launchIn(viewModelScope + collectJob)

        carsRequest
            .debounce(SEARCH_TIMEOUT)
            .flatMapLatest { (min, max) ->
                getNearCarsUseCase(min, max)
            }.flowOn(Dispatchers.IO)
            .onEach { result ->
                _carsResponse.emit(result)
            }.launchIn(viewModelScope + collectJob)
    }

    fun stopCollect() {
        collectJob.cancel()
    }

    fun setPosition(min: Coordinate, max: Coordinate) {
        viewModelScope.launch {
            _carsRequest.emit(Pair(min, max))
        }
    }

    fun setSelected(car: SimpleCar) {
        viewModelScope.launch {
            _selectCar.emit(car)
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            _searchRequest.emit(query)
        }
    }
}
