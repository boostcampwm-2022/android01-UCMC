package com.gta.presentation.ui.cardetail.edit

import androidx.lifecycle.ViewModel
import com.gta.domain.model.AvailableDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarEditViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>>
        get() = _images

    private val _availableDate = MutableStateFlow<AvailableDate?>(AvailableDate())
    val availableDate: StateFlow<AvailableDate?>
        get() = _availableDate

    fun updateImage(img: String) {
        _images.value = listOf(img) + _images.value
    }

    fun deleteImage(position: Int) {
        _images.value = _images.value.filterIndexed { index, _ -> index != position }
    }

    fun setAvailableDate(start: Long, end: Long) {
        _availableDate.value = AvailableDate(start, end)
    }
}
