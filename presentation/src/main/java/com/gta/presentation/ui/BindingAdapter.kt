package com.gta.presentation.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.presentation.R
import com.gta.presentation.model.DateType
import java.text.SimpleDateFormat
import java.util.*

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
