package com.gta.presentation.ui

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.presentation.R
import com.gta.presentation.model.DateType
import com.gta.presentation.model.carDetail.UserState
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("car_type", "car_title")
fun setCarDetailTitle(textView: TextView, type: String, title: String) {
    textView.text = "[$type] $title"
}

@BindingAdapter("set_car_detail_button")
fun setCarDetailBtnState(button: Button, state: UserState) {
    button.text = when (state) {
        UserState.OWNER -> {
            button.isEnabled = true
            button.resources.getString(R.string.correction)
        }
        UserState.RENTED -> {
            button.isEnabled = true
            button.resources.getString(R.string.extension_and_return)
        }
        UserState.USER -> {
            button.isEnabled = true
            button.resources.getString(R.string.reservation)
        }
        UserState.NONE -> {
            button.isEnabled = false
            button.resources.getString(R.string.reservation)
        }
    }
}

@BindingAdapter("selection", "date_type")
fun setReservationTime(textView: TextView, selection: Pair<Long, Long>?, dateType: DateType) {
    when (dateType) {
        DateType.RANGE -> {
            textView.text = selection?.let {
                val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
                "${dateFormat.format(selection.first)} ~ ${dateFormat.format(selection.second)}"
            } ?: textView.resources.getString(R.string.placeholder_date_range)
        }
        DateType.DAY_COUNT -> {
            textView.text = selection?.let {
                val dayTimeUnit = 86400000L
                val dayCount = (selection.second - selection.first).div(dayTimeUnit).plus(1)
                String.format(textView.resources.getString(R.string.total_time), dayCount)
            } ?: textView.resources.getString(R.string.placeholder_date_count)
        }
    }
}
