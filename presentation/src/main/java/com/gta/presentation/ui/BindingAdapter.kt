package com.gta.presentation.ui

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.NotificationType
import com.gta.domain.model.ReservationState
import com.gta.domain.usecase.cardetail.UseState
import com.gta.presentation.R
import com.gta.presentation.model.DateType
import com.gta.presentation.util.DateUtil
import com.gta.presentation.util.FirebaseUtil

@BindingAdapter("image_uri")
fun bindImageUri(view: ImageView, uri: String?) {
    GlideApp.with(view.context)
        .load(uri)
        .placeholder(R.color.neutral80)
        .error(R.drawable.ic_broken_image)
        .fallback(R.drawable.ic_logo)
        .into(view)
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
                "${DateUtil.dateFormat.format(selection.start)} ~ ${DateUtil.dateFormat.format(selection.end)}"
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

@BindingAdapter("text_AvailableDate")
fun setAvailableDateText(textView: TextView, availableDate: AvailableDate?) {
    textView.text = if (availableDate != null && !(availableDate.start == 0L && availableDate.end == 0L)) {
        String.format(
            textView.resources.getString(R.string.car_edit_rent_available_day_format),
            DateUtil.dateFormat.format(availableDate.start),
            DateUtil.dateFormat.format(availableDate.end)
        )
    } else {
        textView.resources.getString(R.string.car_edit_rent_unavailable)
    }
}

@BindingAdapter("text_notification_list_title")
fun setNotificationListItemTitle(textView: TextView, type: NotificationType) {
    textView.text =
        when (type) {
            NotificationType.REQUEST_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_request_title)
            }
            NotificationType.ACCEPT_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_accept_title)
            }
            NotificationType.DECLINE_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_reject_title)
            }
            NotificationType.RETURN_CAR -> {
                textView.resources.getString(R.string.notification_list_return_title)
            }
        }
}

@BindingAdapter(
    "text_notification_list_body_type",
    "text_notification_list_body_from",
    "text_notification_list_body_car"
)
fun setNotificationListItemBody(
    textView: TextView,
    type: NotificationType,
    from: String,
    car: String
) {
    textView.text =
        when (type) {
            NotificationType.REQUEST_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_request_message, from, car)
            }
            NotificationType.ACCEPT_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_accept_message, from, car)
            }
            NotificationType.DECLINE_RESERVATION -> {
                textView.resources.getString(R.string.notification_list_reject_message, from, car)
            }
            NotificationType.RETURN_CAR -> {
                textView.resources.getString(R.string.notification_list_return_message, from, car)
            }
        }
}

@BindingAdapter("reservation_state")
fun setReservationState(textView: TextView, reservationState: ReservationState) {
    textView.text =
        when (reservationState) {
            ReservationState.CANCEL -> {
                textView.resources.getString(R.string.cancel)
            }
            ReservationState.PENDING -> {
                textView.resources.getString(R.string.pending)
            }
            ReservationState.ACCEPT -> {
                textView.resources.getString(R.string.accept)
            }
            ReservationState.RENTING -> {
                textView.resources.getString(R.string.renting)
            }
            ReservationState.DONE -> {
                textView.resources.getString(R.string.return_completed)
            }
        }
}

@BindingAdapter("when_other_user_visible")
fun setVisibilityOtherUserView(view: View, otherId: String) {
    if (FirebaseUtil.uid == otherId) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
    }
}
