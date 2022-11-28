package com.gta.presentation.ui.cardetail.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.edit.UpdateCarDetailDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarEditViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase,
    private val updateCarDetailDataUseCase: UpdateCarDetailDataUseCase
) : ViewModel() {

    private val carId: String

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>>
        get() = _images

    val price = MutableStateFlow("")
    val comment = MutableStateFlow("")
    val rentState = MutableStateFlow(false)

    private val _availableDate = MutableStateFlow<AvailableDate>(AvailableDate())
    val availableDate: StateFlow<AvailableDate>
        get() = _availableDate

    private val _updateState = MutableSharedFlow<Boolean>(replay = 0)
    val updateState: SharedFlow<Boolean>
        get() = _updateState

    init {
        carId = args.get<String>("CAR_ID") ?: "정보 없음"

        viewModelScope.launch {
            val carInfo = getCarDetailDataUseCase(carId).first()
            _images.value = carInfo.images

            price.value = carInfo.price.toString()
            comment.value = carInfo.comment

            rentState.value = when (carInfo.rentState) {
                RentState.AVAILABLE -> true
                else -> false
            }
            _availableDate.value = carInfo.availableDate
        }
    }

    fun updateImage(img: String) {
        _images.value = listOf(img) + _images.value
    }

    fun deleteImage(position: Int) {
        _images.value = _images.value.filterIndexed { index, _ -> index != position }
    }

    fun setAvailableDate(start: Long, end: Long) {
        _availableDate.value = AvailableDate(start, end)
    }

    fun updateData() {
        // TODO 예외처리
        // TODO 대여반납 위치 update 하기
        viewModelScope.launch {
            _updateState.emit(
                updateCarDetailDataUseCase(
                    carId,
                    images.value,
                    price.value.toInt(),
                    comment.value,
                    if (rentState.value) RentState.AVAILABLE else RentState.UNAVAILABLE,
                    availableDate.value,
                    "수정된 위치",
                    Coordinate(37.3588798, 127.1051933)
                ).first()
            )
        }
    }
}
