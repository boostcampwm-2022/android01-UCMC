package com.gta.presentation.ui.cardetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CarDetailEditViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>>
        get() = _images

    fun updateImage(img: String) {
        _images.value = listOf(img) + _images.value
    }

    fun deleteImage(position: Int) {
        _images.value = _images.value.filterIndexed { index, _ -> index != position }
    }
}
