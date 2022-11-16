package com.gta.presentation.ui

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.presentation.R
import com.gta.presentation.model.carDetail.UserState

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
