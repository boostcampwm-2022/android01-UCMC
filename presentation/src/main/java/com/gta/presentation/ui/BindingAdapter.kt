package com.gta.presentation.ui

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.presentation.R
import com.gta.presentation.model.carDetail.BtnType

@BindingAdapter("car_type", "car_title")
fun setCarDetailTitle(textView: TextView, type: String, title: String) {
    textView.text = "[$type] $title"
}

@BindingAdapter("set_car_detail_btn_state")
fun setCarDetailBtnState(button: Button, state: BtnType) {
    button.text = when (state) {
        BtnType.OWNER -> {
            button.resources.getString(R.string.correction)
        }
        BtnType.RENTED -> {
            button.resources.getString(R.string.extension_and_return)
        }
        BtnType.USER -> {
            button.resources.getString(R.string.reservation)
        }
        BtnType.NONE -> {
            button.isClickable = false
            button.resources.getString(R.string.reservation)
        }
    }
}
