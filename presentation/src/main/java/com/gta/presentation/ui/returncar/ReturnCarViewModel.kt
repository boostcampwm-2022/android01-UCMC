package com.gta.presentation.ui.returncar

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.UCMCResult
import com.gta.domain.usecase.cardetail.GetNowRentCarUseCase
import com.gta.domain.usecase.returncar.ReturnCarUseCase
import com.gta.presentation.R
import com.gta.presentation.util.DateUtil
import com.gta.presentation.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ReturnCarViewModel @Inject constructor(
    @ApplicationContext context: Context,
    args: SavedStateHandle,
    getNowRentCarUseCase: GetNowRentCarUseCase,
    private val returnCarUseCase: ReturnCarUseCase
) : ViewModel() {
    private val carId = args.get<String>("carId") ?: ""

    val simpleReservation: StateFlow<SimpleReservation> =
        getNowRentCarUseCase(uid = FirebaseUtil.uid, carId = carId).map {
            // TODO 예외 처리
            if (it is UCMCResult.Success) {
                it.data
            } else {
                SimpleReservation()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SimpleReservation()
        )

    private val _remainTime = MutableStateFlow("-")
    val remainTime: StateFlow<String> get() = _remainTime

    private val _returnCarEvent = MutableSharedFlow<String>()
    val returnCarEvent: SharedFlow<String> get() = _returnCarEvent

    private val minuteFormat: String by lazy { context.getString(R.string.day_hour_format) }
    private val hourFormat: String by lazy { context.getString(R.string.hour_format) }
    private val dayHourFormat: String by lazy { context.getString(R.string.day_hour_format) }

    fun emitRemainTime() {
        val endTime = simpleReservation.value.reservationDate.end.also {
            if (it == 0L) {
                return
            }
        }
        val startTime = System.currentTimeMillis()

        viewModelScope.launch {
            _remainTime.emit(calcRemainTime(startTime, endTime))
        }
    }

    private fun calcRemainTime(startTime: Long, endTime: Long): String {
        val time = abs(endTime - startTime) / DateUtil.MINUTE_UNIT
        val remainDay = time / (60 * 24)
        val remainHour = time % (60 * 24) / 60
        val remainMinute = time % (60 * 24) % 60

        return when {
            remainDay == 0L -> {
                String.format(hourFormat, remainHour)
            }
            remainHour == 0L -> {
                String.format(minuteFormat, remainMinute)
            }
            else -> {
                String.format(dayHourFormat, remainDay, remainHour)
            }
        }
    }

    fun returnRentedCar() {
        val reservationId = simpleReservation.value.reservationId

        viewModelScope.launch {
            if (returnCarUseCase(reservationId, carId, FirebaseUtil.uid)) {
                _returnCarEvent.emit(reservationId)
            }
        }
    }
}
