package com.gta.presentation.ui.returncar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleReservation
import com.gta.domain.usecase.returncar.GetNowSimpleReservationUseCase
import com.gta.presentation.util.DateUtil
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ReturnCarViewModel @Inject constructor(
    args: SavedStateHandle,
    getNowSimpleReservationUseCase: GetNowSimpleReservationUseCase
) : ViewModel() {
    private val carId = args.get<String>("carId") ?: ""

    val simpleReservation: StateFlow<SimpleReservation?> =
        getNowSimpleReservationUseCase(uid = FirebaseUtil.uid, carId = carId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _remainTime = MutableStateFlow("-")
    val remainTime: StateFlow<String> get() = _remainTime

    fun emitRemainTime() {
        val endTime = simpleReservation.value?.reservationDate?.end ?: return
        val startTime = System.currentTimeMillis()
        val time = abs(endTime - startTime) / DateUtil.HOUR_UNIT

        viewModelScope.launch {
            _remainTime.emit(String.format("%d일 %d시간", time / 24, time % 24))
        }
    }
}
