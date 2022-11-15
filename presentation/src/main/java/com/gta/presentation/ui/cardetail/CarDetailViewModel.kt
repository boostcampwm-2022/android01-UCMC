package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarState
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.IsMyCarUseCase
import com.gta.domain.usecase.cardetail.IsNowMyRentCarUseCase
import com.gta.presentation.model.carDetail.BtnType
import com.gta.presentation.model.carDetail.CarInfo
import com.gta.presentation.model.carDetail.CarOwner
import com.gta.presentation.model.carDetail.PriceType
import com.gta.presentation.model.carDetail.toCarInfo
import com.gta.presentation.model.carDetail.toCarOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    isMyCarUseCase: IsMyCarUseCase,
    isNowMyRentCarUseCase: IsNowMyRentCarUseCase,
    getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {

    val car: StateFlow<CarInfo>

    val owner: StateFlow<CarOwner>

    private val _btnState = MutableStateFlow(BtnType.User)
    val btnState: StateFlow<BtnType>
        get() = _btnState

    init {
        // TODO : Safe Args 연결
        val carId = args.get<String>("CAR_ID") ?: "debug"
        val data: Flow<CarDetail> = getCarDetailDataUseCase(carId)

        car = data.map { it.toCarInfo() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarInfo(
                "",
                "",
                CarState.Unavailable,
                "",
                "",
                PriceType.Day,
                0,
                "없음",
                emptyList()
            )
        )

        owner = data.map { it.toCarOwner() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarOwner("", "", "0", "")
        )

        viewModelScope.launch {
            car.collectLatest {
                _btnState.value = if (isMyCarUseCase(carId)) {
                    BtnType.Owner
                } else if (isNowMyRentCarUseCase(carId)) {
                    BtnType.Rented
                } else if (it.state != CarState.Unavailable) {
                    BtnType.User
                } else {
                    BtnType.NONE
                }
            }
        }
    }

    fun onReportClick() {
        Timber.d("자동차 상세페이지 신고")
    }
}
