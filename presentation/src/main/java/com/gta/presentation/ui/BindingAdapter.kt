package com.gta.presentation.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("car_type", "car_title")
fun setCarDetailTitle(textView: TextView, type: String, title: String) {
    textView.text = "[$type] $title"
}
