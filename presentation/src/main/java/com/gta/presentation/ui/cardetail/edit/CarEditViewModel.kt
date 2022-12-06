package com.gta.presentation.ui.cardetail.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.edit.UpdateCarDetailDataUseCase
import com.gta.domain.usecase.cardetail.edit.UploadCarImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UpdateState {
    NORMAL, LOAD, SUCCESS, FAIL
}

@HiltViewModel
class CarEditViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase,
    private val uploadCarImagesUseCase: UploadCarImagesUseCase,
    private val updateCarDetailDataUseCase: UpdateCarDetailDataUseCase
) : ViewModel() {

    private val carId: String

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>>
        get() = _images

    val price = MutableStateFlow("")
    val comment = MutableStateFlow("")
    val rentState = MutableStateFlow(false)

    private val _availableDate = MutableStateFlow(AvailableDate())
    val availableDate: StateFlow<AvailableDate>
        get() = _availableDate

    private val _location = MutableStateFlow<String>("정보 없음")
    val location: StateFlow<String>
        get() = _location

    var coordinate: Coordinate? = null
        private set

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.NORMAL)
    val updateState: StateFlow<UpdateState>
        get() = _updateState

    // 초기 이미지
    private var initImage: List<String> = emptyList()

    val defaultCoordinate = Coordinate(37.3588798, 127.1051933)

    init {
        carId = args.get<String>("CAR_ID") ?: "정보 없음"

        viewModelScope.launch {
            val carInfo = getCarDetailDataUseCase(carId).first()
            if (carInfo is UCMCResult.Success) {
                _images.value = carInfo.data.images
                initImage = carInfo.data.images

                price.value = carInfo.data.price.toString()
                comment.value = carInfo.data.comment

                rentState.value = carInfo.data.rentState == RentState.AVAILABLE
                _availableDate.value = carInfo.data.availableDate
                _location.value = carInfo.data.location
                coordinate = carInfo.data.coordinate
            }
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

    fun setLocationData(text: String, latitude: Double, longitude: Double) {
        _location.value = text
        coordinate = Coordinate(latitude, longitude)
    }

    fun updateData() {
        _updateState.value = UpdateState.LOAD

        viewModelScope.launch {
            _updateState.value =
                if (
                    updateCarDetailDataUseCase(
                        carId,
                        uploadCarImagesUseCase(carId, initImage, images.value).first(),
                        price.value.toInt(),
                        comment.value,
                        if (rentState.value) RentState.AVAILABLE else RentState.UNAVAILABLE,
                        availableDate.value,
                        location.value,
                        coordinate ?: defaultCoordinate
                    ).first()
                ) {
                    UpdateState.SUCCESS
                } else {
                    UpdateState.FAIL
                }
        }
    }
}
