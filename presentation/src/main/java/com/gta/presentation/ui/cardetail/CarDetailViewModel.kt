package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.IsMyCarUseCase
import com.gta.domain.usecase.cardetail.IsNowMyRentCarUseCase
import com.gta.presentation.model.CarOwner
import com.gta.presentation.model.PriceType
import com.gta.presentation.model.toCarOwner
import com.gta.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.* // ktlint-disable no-wildcard-imports
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    isMyCarUseCase: IsMyCarUseCase,
    isNowMyRentCarUseCase: IsNowMyRentCarUseCase,
    getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {

    val car: StateFlow<com.gta.presentation.model.CarDetail>

    val owner: StateFlow<CarOwner>

    init {
        // TODO : Safe Args 연결
        val carInfo: Flow<CarDetail> =
            getCarDetailDataUseCase(args.get<String>("CAR_ID") ?: "debug")

        car = carInfo.map { it.toPresentation() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.gta.presentation.model.CarDetail(
                "", "", "", "", "", PriceType.Day, 0, "없음", emptyList()
            )
        )

        owner = carInfo.map { it.toCarOwner() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarOwner("", "", 0, "")
        )
    }

    fun onReportClick() {
        Timber.d("자동차 상세페이지 신고")
    }
}
