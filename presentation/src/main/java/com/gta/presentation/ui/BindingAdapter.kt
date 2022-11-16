package com.gta.presentation.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.presentation.R
import com.gta.presentation.model.DateType
import com.gta.presentation.ui.reservation.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("selection", "date_type")
fun setReservationTime(textView: TextView, selection: Pair<Long, Long>?, dateType: DateType) {
    selection ?: return
    when(dateType) {
        DateType.RANGE -> {
            val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
            textView.text = "${dateFormat.format(selection.first)} ~ ${dateFormat.format(selection.second)}"
        }
        DateType.DAY_COUNT -> {
            val dayTimeUnit = 86400000L
            val dayCount = (selection.second - selection.first).div(dayTimeUnit).plus(1)
            textView.text = String.format(textView.resources.getString(R.string.total_time), dayCount)
        }
    }
}
