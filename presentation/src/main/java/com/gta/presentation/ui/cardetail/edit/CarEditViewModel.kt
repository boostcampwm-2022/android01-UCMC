package com.gta.presentation.ui.cardetail.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.RentState
import com.gta.domain.usecase.cardetail.GetCarDetailDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CarEditViewModel @Inject constructor(
    args: SavedStateHandle,
    getCarDetailDataUseCase: GetCarDetailDataUseCase
) : ViewModel() {
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>>
        get() = _images

    val price = MutableStateFlow("")
    val comment = MutableStateFlow("")
    val rentState = MutableStateFlow(false)

    private val _availableDate = MutableStateFlow<AvailableDate?>(AvailableDate())
    val availableDate: StateFlow<AvailableDate?>
        get() = _availableDate

    init {
        val carId = args.get<String>("CAR_ID") ?: "정보 없음"

        viewModelScope.launch {
            val carInfo = getCarDetailDataUseCase(carId).first()
            _images.value = carInfo.images

            Timber.d("carInfo.price ${carInfo.price}")
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
        // 이미지
        // 가격
        // 코멘트
        // 대여 가능 날짜(가능/불가능도 체크)
        // 대여 반납 위치(적용안됨)
        Timber.d("image ${images.value}")
        Timber.d("price ${price.value}")
        Timber.d("comment ${comment.value}")
        Timber.d("able  ${rentState.value}")
        Timber.d("날짜  ${availableDate.value}")
    }
}
