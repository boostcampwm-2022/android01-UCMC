package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarState
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.GetCarOwnerIdUseCase
import com.gta.domain.usecase.cardetail.GetMyUserIdUserCase
import com.gta.domain.usecase.cardetail.GetNowRentUserIdUseCase
import com.gta.presentation.model.carDetail.CarInfo
import com.gta.presentation.model.carDetail.CarOwner
import com.gta.presentation.model.carDetail.PriceType
import com.gta.presentation.model.carDetail.UserState
import com.gta.presentation.model.carDetail.toCarInfo
import com.gta.presentation.model.carDetail.toCarOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    private val getNowRentUserIdUseCase: GetNowRentUserIdUseCase,
    private val getCarOwnerIdUseCase: GetCarOwnerIdUseCase,
    private val getMyUserIdUserCase: GetMyUserIdUserCase,
    getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {

    private var carId: String
    val car: StateFlow<CarInfo>

    val owner: StateFlow<CarOwner>

    private val _userState = MutableStateFlow(UserState.NONE)
    val userState: StateFlow<UserState>
        get() = _userState

    init {
        // TODO : Safe Args 연결
        carId = args.get<String>("CAR_ID") ?: "debug"
        val data: Flow<CarDetail> = getCarDetailDataUseCase(carId)

        car = data.map { it.toCarInfo() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarInfo(
                "",
                "",
                CarState.UNAVAILABLE,
                "",
                "",
                PriceType.DAY,
                0,
                "없음",
                emptyList()
            )
        )

        owner = data.map { it.toCarOwner() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarOwner("", "", 0f, "")
        )
    }

    fun getPageState() {
        _userState.value = when (getMyUserIdUserCase()) {
            getCarOwnerIdUseCase(carId) -> {
                UserState.OWNER
            }
            getNowRentUserIdUseCase(carId) -> {
                UserState.RENTED
            }
            else -> {
                if (car.value.state != CarState.UNAVAILABLE) {
                    UserState.USER
                } else {
                    UserState.NONE
                }
            }
        }
    }

    fun onReportClick() {
        Timber.d("자동차 상세페이지 신고")
    }
}
