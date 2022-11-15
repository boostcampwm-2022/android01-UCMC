package com.gta.presentation.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("selection")
fun setReservationTime(textView: TextView, selection: Pair<Long, Long>?) {
    selection ?: return
    val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
    textView.text = "${dateFormat.format(selection.first)} ~ ${dateFormat.format(selection.second)}"
}

