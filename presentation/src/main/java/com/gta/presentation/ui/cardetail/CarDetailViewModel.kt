package com.gta.presentation.ui.cardetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.CarDetail
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.GetUseStateAboutCarUseCase
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase,
    getUseStateAboutCarUseCase: GetUseStateAboutCarUseCase
) : ViewModel() {

    val carInfo: StateFlow<CarDetail>
    val useState: StateFlow<UseState>

    init {
        val carId = args.get<String>("CAR_ID") ?: "정보 없음"
        val uid = FirebaseUtil.uid

        carInfo = getCarDetailDataUseCase(carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarDetail()
        )

        useState = getUseStateAboutCarUseCase(uid, carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UseState.UNAVAILABLE
        )
    }

    fun onReportClick() {
        Timber.d("자동차 상세페이지 신고")
    }
}
