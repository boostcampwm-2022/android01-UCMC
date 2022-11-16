package com.gta.presentation.util

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.parcelize.Parcelize

@Parcelize
class DateValidator(
    private val reservationRange: Pair<Long, Long>,
    private val inValidList: List<Pair<Long, Long>>
) : CalendarConstraints.DateValidator {

    override fun isValid(date: Long): Boolean {
        return date in reservationRange.first..reservationRange.second && date >= MaterialDatePicker.todayInUtcMilliseconds() && checkInvalidList(
            date
        )
    }

    private fun checkInvalidList(date: Long): Boolean {
        for (range in inValidList) {
            if (date in range.first..range.second) {
                return false
            }
        }
        return true
    }
}
