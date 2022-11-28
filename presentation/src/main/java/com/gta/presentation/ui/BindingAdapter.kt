package com.gta.presentation.ui

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.domain.model.AvailableDate
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.R
import com.gta.presentation.model.DateType
import com.gta.presentation.util.DateUtil
import java.text.DecimalFormat

private val dec = DecimalFormat("#,###")

@BindingAdapter("image_uri")
fun bindImageUri(view: ImageView, uri: String) {
    GlideApp.with(view.context)
        .load(uri)
        .placeholder(R.color.neutral80)
        .error(R.drawable.ic_broken_image)
        .fallback(R.drawable.ic_logo)
        .into(view)
}

@BindingAdapter("price")
fun price(textView: TextView, price: Int) {
    textView.text = textView.context.getString(R.string.price, dec.format(price))
}

@BindingAdapter("car_type", "car_year", "car_title")
fun setCarDetailTitle(textView: TextView, type: String, year: Int, title: String) {
    textView.text = "[$type] $year $title"
}

@BindingAdapter("set_car_detail_button")
fun setCarDetailBtnState(button: Button, state: UseState) {
    button.isEnabled = true
    button.text = when (state) {
        UseState.OWNER -> {
            button.resources.getString(R.string.correction)
        }
        UseState.NOW_RENT_USER -> {
            button.resources.getString(R.string.extension_and_return)
        }
        UseState.USER -> {
            button.resources.getString(R.string.reservation)
        }
        UseState.UNAVAILABLE -> {
            button.isEnabled = false
            button.resources.getString(R.string.reservation)
        }
    }
}

@BindingAdapter("selection", "date_type")
fun setReservationTime(textView: TextView, selection: AvailableDate?, dateType: DateType) {
    when (dateType) {
        DateType.RANGE -> {
            textView.text = selection?.let {
                "${DateUtil.dateFormat.format(selection.start)} ~ ${
                DateUtil.dateFormat.format(
                    selection.end
                )
                }"
            } ?: textView.resources.getString(R.string.placeholder_date_range)
        }
        DateType.DAY_COUNT -> {
            textView.text = selection?.let {
                String.format(
                    textView.resources.getString(R.string.total_time),
                    DateUtil.getDateCount(it.start, it.end)
                )
            } ?: textView.resources.getString(R.string.placeholder_date_count)
        }
    }
}
