package com.gta.presentation.ui.cardetail.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
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
    NOMAL, LOAD, SUCCESS, FAIL
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

    private val _availableDate = MutableStateFlow<AvailableDate>(AvailableDate())
    val availableDate: StateFlow<AvailableDate>
        get() = _availableDate

    private val _location = MutableStateFlow<String>("정보 없음")
    val location: StateFlow<String>
        get() = _location

    private var _coordinate: Coordinate? = null
    val coordinate: Coordinate?
        get() = _coordinate

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.NOMAL)
    val updateState: StateFlow<UpdateState>
        get() = _updateState

    // 초기 이미지
    private var initImage: List<String> = emptyList()

    init {
        carId = args.get<String>("CAR_ID") ?: "정보 없음"

        viewModelScope.launch {
            val carInfo = getCarDetailDataUseCase(carId).first()
            _images.value = carInfo.images
            initImage = carInfo.images

            price.value = carInfo.price.toString()
            comment.value = carInfo.comment

            rentState.value = when (carInfo.rentState) {
                RentState.AVAILABLE -> true
                else -> false
            }
            _availableDate.value = carInfo.availableDate
            _location.value = carInfo.location
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
        _coordinate = Coordinate(latitude, longitude)
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
                        coordinate ?: Coordinate(37.3588798, 127.1051933)
                    ).first()
                ) {
                    UpdateState.SUCCESS
                } else {
                    UpdateState.FAIL
                }
        }
    }
}
