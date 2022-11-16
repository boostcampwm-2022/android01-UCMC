package com.gta.presentation.ui.reservation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor() : ViewModel() {
    val selectedDateRange = MutableLiveData<Pair<Long, Long>>()
    val selectedInsuranceOption = MutableLiveData<Pair<Int, Int>>()
}